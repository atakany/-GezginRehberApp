package com.example.gezginrehberbt.ui.favorites

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gezginrehberbt.R
import com.example.gezginrehberbt.ui.favorites.FavoritesFragmentDirections
import com.example.gezginrehberbt.data.FavoritesManager
import com.example.gezginrehberbt.data.repository.PlaceRepository
import com.example.gezginrehberbt.databinding.FragmentFavoritesBinding
import com.example.gezginrehberbt.model.Place
import com.example.gezginrehberbt.ui.explore.PlaceAdapter

class FavoritesFragment : Fragment(R.layout.fragment_favorites) {

    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!

    private lateinit var recommendationsAdapter: PlaceAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentFavoritesBinding.bind(view)

        // FavoritesManager'ı başlatıyoruz eğer başlatılmamışsa
        FavoritesManager.initialize(requireContext())
        
        setupRecommendationsRecycler()

        // Favorileri izliyoruz
        FavoritesManager.favorites.observe(viewLifecycleOwner) { favoritesList ->
            binding.tvEmpty.isVisible = favoritesList.isEmpty()
            binding.recyclerFavorites.isVisible = favoritesList.isNotEmpty()

            if (favoritesList.isNotEmpty()) {
                binding.recyclerFavorites.adapter = PlaceAdapter(favoritesList) { place ->
                    val action = FavoritesFragmentDirections.actionNavigationFavoritesToPlaceDetailFragment(place)
                    findNavController().navigate(action)
                }
                // Önerileri güncelle
                updateRecommendations(favoritesList)
            } else {
                // Favori yoksa önerileri gizle
                hideRecommendations()
            }
        }
        
        // İlk yükleme için FavoritesManager'ı yeniliyoruz
        FavoritesManager.refreshFavorites()
    }

    private fun setupRecommendationsRecycler() {
        binding.recyclerRecommendations.layoutManager = LinearLayoutManager(
            requireContext(), 
            LinearLayoutManager.HORIZONTAL, 
            false
        )
        
        recommendationsAdapter = PlaceAdapter(emptyList()) { place ->
            val action = FavoritesFragmentDirections.actionNavigationFavoritesToPlaceDetailFragment(place)
            findNavController().navigate(action)
        }
        binding.recyclerRecommendations.adapter = recommendationsAdapter
    }

    private fun updateRecommendations(favoritesList: List<Place>) {
        if (favoritesList.isEmpty()) {
            hideRecommendations()
            return
        }
        
        // Son favori eklenen mekanı temel alarak öneriler oluştur
        val lastFavorite = favoritesList.last()
        val favoriteIds = favoritesList.map { it.id }.toSet()
        
        // Aynı şehir ve kategorideki mekanlardan favori olmayanları öner
        val recommendations = PlaceRepository.getPlacesByCity(lastFavorite.city)
            .filter { it.id !in favoriteIds && it.category == lastFavorite.category }
            .take(3) // En fazla 3 öneri göster
            
        if (recommendations.isNotEmpty()) {
            binding.tvRecommendationsTitle.isVisible = true
            binding.recyclerRecommendations.isVisible = true
            recommendationsAdapter = PlaceAdapter(recommendations) { place ->
                val action = FavoritesFragmentDirections.actionNavigationFavoritesToPlaceDetailFragment(place)
                findNavController().navigate(action)
            }
            binding.recyclerRecommendations.adapter = recommendationsAdapter
        } else {
            hideRecommendations()
        }
    }

    private fun hideRecommendations() {
        binding.tvRecommendationsTitle.isVisible = false
        binding.recyclerRecommendations.isVisible = false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
