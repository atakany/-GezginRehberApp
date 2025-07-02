package com.example.gezginrehberbt.ui.city

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gezginrehberbt.R
import com.example.gezginrehberbt.data.FavoritesManager
import com.example.gezginrehberbt.data.repository.PlaceRepository
import com.example.gezginrehberbt.databinding.FragmentCitySelectBinding
import com.example.gezginrehberbt.model.Place
import com.example.gezginrehberbt.ui.explore.PlaceAdapter

class CitySelectFragment : Fragment(R.layout.fragment_city_select) {

    private var _binding: FragmentCitySelectBinding? = null
    private val binding get() = _binding!!

    private lateinit var recommendationsAdapter: PlaceAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentCitySelectBinding.bind(view)

        // FavoritesManager'ı başlat
        FavoritesManager.initialize(requireContext())

        binding.cardIstanbul.setOnClickListener { openExplore("İstanbul") }
        binding.cardAnkara.setOnClickListener { openExplore("Ankara") }
        binding.cardIzmir.setOnClickListener { openExplore("İzmir") }

        // FavoritesManager'dan favorileri izle
        FavoritesManager.favorites.observe(viewLifecycleOwner) { _ ->
            setupRecommendations()
        }
        
        // İlk yükleme için önerileri ayarla
        setupRecommendations()
    }

    private fun setupRecommendations() {
        val recommendedPlaces = FavoritesManager.getRecommendedPlaces()

        if (recommendedPlaces.isEmpty()) {
            binding.tvRecommendationsTitle.visibility = View.GONE
            binding.rvRecommendations.visibility = View.GONE
            return
        }

        binding.tvRecommendationsTitle.visibility = View.VISIBLE
        binding.rvRecommendations.visibility = View.VISIBLE

        recommendationsAdapter = PlaceAdapter(recommendedPlaces) { place ->
            val action = CitySelectFragmentDirections.actionCitySelectFragmentToPlaceDetailFragment(place)
            findNavController().navigate(action)
        }
        binding.rvRecommendations.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.rvRecommendations.adapter = recommendationsAdapter
    }

    private fun openExplore(city: String) {
        val bundle = Bundle().apply { putString("city", city) }
        findNavController().navigate(R.id.action_citySelect_to_navigation_explore, bundle)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
