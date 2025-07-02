package com.example.gezginrehberbt.ui.placedetail

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.gezginrehberbt.R
import com.example.gezginrehberbt.data.repository.PlaceRepository
import com.example.gezginrehberbt.databinding.FragmentPlaceDetailBinding
import com.example.gezginrehberbt.model.Comment
import com.example.gezginrehberbt.model.Place
import com.example.gezginrehberbt.ui.placedetail.FullscreenMapDialog
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.util.UUID

class PlaceDetailFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentPlaceDetailBinding? = null
    private val binding get() = _binding!!

    private val args: PlaceDetailFragmentArgs by navArgs()
    private lateinit var place: Place
    private lateinit var googleMap: GoogleMap
    private lateinit var placeRepository: PlaceRepository
    private var isFavorite = false

    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var commentAdapter: CommentAdapter
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var map: GoogleMap? = null
    
    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) ||
                    permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                // Precise location access granted
                enableMyLocation()
            }
            else -> {
                // No location access granted
                showMessage("Konum erişimi reddedildi")
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaceDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentPlaceDetailBinding.bind(view)

        place = args.place
        placeRepository = PlaceRepository.getInstance(requireContext())
        isFavorite = placeRepository.isFavorite(place.id)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        setupToolbar()
        setupViews(place)
        setupClickListeners()
        updateFavoriteButton()
        setupMap()

        // Firebase ve Yorumlar kurulumu
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        setupCommentsRecyclerView()
        loadComments()
    }
    
    private fun setupMap() {
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
        mapFragment?.getMapAsync { googleMap ->
            map = googleMap
            
            // Harita ayarlarını yap
            googleMap.uiSettings.apply {
                isZoomControlsEnabled = true
                isCompassEnabled = true
                isMapToolbarEnabled = true
                isZoomGesturesEnabled = true
                isScrollGesturesEnabled = true
                isTiltGesturesEnabled = false
                isRotateGesturesEnabled = false
            }
            
            // Konum izinlerini kontrol et
            enableMyLocation()
            
            // Yer işaretini ekle
            val placeLocation = LatLng(place.latitude, place.longitude)
            googleMap.addMarker(
                MarkerOptions()
                    .position(placeLocation)
                    .title(place.name)
            )
            
            // Kamerayı konuma odakla
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(placeLocation, 15f))
            
            // Harita yüklendikten sonra boyutunu güncelle
            view?.post {
                mapFragment.view?.let { mapView ->
                    val params = mapView.layoutParams
                    val screenWidth = resources.displayMetrics.widthPixels
                    params.height = (screenWidth * 0.6).toInt().coerceAtLeast(160)
                    mapView.layoutParams = params
                }
            }
        }
    }
    
    private fun enableMyLocation() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            map?.isMyLocationEnabled = true
        } else {
            // Konum izni iste
            locationPermissionRequest.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener { findNavController().navigateUp() }
    }

    private fun setupViews(place: Place) {
        binding.apply {
            // Set place details
            tvPlaceName.text = place.name
            tvPlaceLocation.text = place.address
            tvCategory.text = place.category
            ratingBar.rating = place.rating.toFloat()
            tvRating.text = place.rating.toString()
            
            // Load image with Glide - using fixed height from XML
            Glide.with(this@PlaceDetailFragment)
                .load(place.imageResId)
                .centerCrop()
                .into(ivPlaceImage)
                
            // Set up map height based on screen size
            view?.post {
                val screenWidth = resources.displayMetrics.widthPixels
                val mapHeight = (screenWidth * 0.6).toInt()
                
                // Update map container height
                val mapContainer = binding.root.findViewById<View>(R.id.map)
                val params = mapContainer?.layoutParams
                params?.height = mapHeight.coerceAtLeast(160) // Minimum height
                mapContainer?.layoutParams = params
            }
        }
    }

    private fun setupClickListeners() {
        binding.apply {
            btnDirections.setOnClickListener {
                // Open directions in Google Maps
                try {
                    val gmmIntentUri = Uri.parse("google.navigation:q=${place.latitude},${place.longitude}")
                    val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                    mapIntent.setPackage("com.google.android.apps.maps")
                    
                    // Check if Google Maps is installed
                    if (mapIntent.resolveActivity(requireActivity().packageManager) != null) {
                        startActivity(mapIntent)
                    } else {
                        // If Google Maps is not installed, open in browser
                        val webIntent = Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://www.google.com/maps/dir/?api=1&destination=${place.latitude},${place.longitude}")
                        )
                        startActivity(webIntent)
                    }
                } catch (e: Exception) {
                    showMessage("Yol tarifi açılırken bir hata oluştu")
                }
            }

            btnFavorite.setOnCheckedChangeListener { _, isChecked ->
                isFavorite = isChecked
                val message = if (isChecked) {
                    placeRepository.addFavorite(place.id)
                    "${place.name} favorilere eklendi"
                } else {
                    placeRepository.removeFavorite(place.id)
                    "${place.name} favorilerden kaldırıldı"
                }

                updateFavoriteButton()
                showMessage(message)
            }

            btnAddComment.setOnClickListener {
                // Show add comment dialog
                showAddCommentDialog()
            }
            
            // Haritayı tam ekran yap butonu
            btnExpandMap.setOnClickListener {
                FullscreenMapDialog.newInstance(place).show(
                    parentFragmentManager,
                    FullscreenMapDialog::class.java.simpleName
                )
            }
        }
    }

    private fun setupCommentsRecyclerView() {
        commentAdapter = CommentAdapter(emptyList())
        binding.rvComments.layoutManager = LinearLayoutManager(requireContext())
        binding.rvComments.adapter = commentAdapter
    }

    private fun loadComments() {
        firestore.collection("places").document(place.id).collection("comments")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { result ->
                val comments = result.toObjects(Comment::class.java)
                commentAdapter.updateComments(comments)
            }
            .addOnFailureListener { exception ->
                Toast.makeText(requireContext(), "Yorumlar yüklenemedi: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateFavoriteButton() {
        // The toggle button's appearance is already handled by the selector_favorite_toggle drawable
        // We just need to update the checked state
        binding.btnFavorite.isChecked = isFavorite
    }
    
    private fun showAddCommentDialog() {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            showMessage("Yorum yapmak için giriş yapmalısınız.")
            return
        }

        // Yorum ekleme dialog'unu oluştur
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_comment, null)
        val editTextComment = dialogView.findViewById<TextInputEditText>(R.id.et_comment_text)
        val ratingBar = dialogView.findViewById<RatingBar>(R.id.ratingBar_add_comment)
        
        val alertDialog = androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Yorum Ekle")
            .setView(dialogView)
            .setPositiveButton("Gönder") { dialog, _ ->
                // Boş kontrol
                val commentText = editTextComment.text.toString()
                val rating = ratingBar.rating
                
                if (commentText.isBlank()) {
                    showMessage("Yorum metni boş olamaz!")
                    return@setPositiveButton
                }
                
                if (rating <= 0) {
                    showMessage("Lütfen bir puan veriniz!")
                    return@setPositiveButton
                }
                
                // Yorumu Firebase'e ekle
                addCommentToFirebase(commentText, rating)
                dialog.dismiss()
            }
            .setNegativeButton("İptal") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            
        alertDialog.show()
    }
    
    private fun addCommentToFirebase(commentText: String, rating: Float) {
        val currentUser = auth.currentUser ?: return
        
        // İşlem başladığında yükleniyor göstergesi
        binding.progressBarComments.visibility = View.VISIBLE
        
        // Benzersiz bir yorum kimliği oluştur
        val commentId = UUID.randomUUID().toString()
        
        // Yorum nesnesini oluştur
        val comment = Comment(
            id = commentId,
            placeId = place.id,
            userId = currentUser.uid,
            userName = currentUser.displayName ?: "Misafir",
            rating = rating,
            text = commentText
            // timestamp Firebase tarafından otomatik oluşturulacak
        )
        
        // Firebase'e yorum ekle
        firestore.collection("places")
            .document(place.id)
            .collection("comments")
            .document(commentId)
            .set(comment)
            .addOnSuccessListener {
                showMessage("Yorum başarıyla eklendi!")
                // Yorumları yeniden yükle
                loadComments()
                binding.progressBarComments.visibility = View.GONE
            }
            .addOnFailureListener { exception ->
                showMessage("Yorum eklenirken hata oluştu: ${exception.message}")
                binding.progressBarComments.visibility = View.GONE
            }
    }

    private fun showMessage(message: String) {
        view?.let {
            Snackbar.make(it, message, Snackbar.LENGTH_SHORT).show()
        }
    }

    override fun onMapReady(map: GoogleMap) {
        this.map = map
        // Harita hazır olduğunda yapılacak işlemler
        setupMap()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
