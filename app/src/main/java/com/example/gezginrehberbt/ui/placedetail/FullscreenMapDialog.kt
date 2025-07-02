package com.example.gezginrehberbt.ui.placedetail

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.gezginrehberbt.R
import com.example.gezginrehberbt.databinding.DialogFullscreenMapBinding
import com.example.gezginrehberbt.model.Place
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class FullscreenMapDialog : DialogFragment(), OnMapReadyCallback {

    private var _binding: DialogFullscreenMapBinding? = null
    private val binding get() = _binding!!
    private lateinit var place: Place
    private var googleMap: GoogleMap? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogFullscreenMapBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).apply {
            // Make the dialog fullscreen
            window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Get place from arguments
        place = arguments?.getParcelable(ARG_PLACE) ?: return dismiss()
        
        // Set up toolbar
        binding.toolbar.title = place.name
        binding.toolbar.setNavigationOnClickListener { dismiss() }
        
        // Set up map
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
        mapFragment?.getMapAsync(this)
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        
        // Configure map
        map.uiSettings.isZoomControlsEnabled = true
        map.uiSettings.isCompassEnabled = true
        map.uiSettings.isMapToolbarEnabled = true
        
        // Add marker and move camera
        val placeLocation = LatLng(place.latitude, place.longitude)
        map.addMarker(
            MarkerOptions()
                .position(placeLocation)
                .title(place.name)
        )
        
        // Move camera to the location
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(placeLocation, 15f))
        
        // Add directions button click listener
        binding.btnDirections.setOnClickListener {
            openDirections(place.latitude, place.longitude)
        }
    }
    
    private fun openDirections(latitude: Double, longitude: Double) {
        try {
            val gmmIntentUri = Uri.parse("google.navigation:q=$latitude,$longitude")
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            mapIntent.setPackage("com.google.android.apps.maps")
            
            // Check if Google Maps is installed
            if (mapIntent.resolveActivity(requireActivity().packageManager) != null) {
                startActivity(mapIntent)
            } else {
                // If Google Maps is not installed, open in browser
                val webIntent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://www.google.com/maps/dir/?api=1&destination=$latitude,$longitude")
                )
                startActivity(webIntent)
            }
        } catch (e: Exception) {
            // Handle any errors
            showMessage("Yol tarifi açılırken bir hata oluştu")
        }
    }
    
    private fun showMessage(message: String) {
        // You can show a toast or snackbar here
        android.widget.Toast.makeText(context, message, android.widget.Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_PLACE = "arg_place"
        
        fun newInstance(place: Place): FullscreenMapDialog {
            return FullscreenMapDialog().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_PLACE, place)
                }
            }
        }
    }
}
