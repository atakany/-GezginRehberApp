package com.example.gezginrehberbt.ui.explore

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.gezginrehberbt.R
import com.example.gezginrehberbt.data.repository.PlaceRepository
import com.example.gezginrehberbt.databinding.FragmentExploreBinding
import com.example.gezginrehberbt.model.Place

class ExploreFragment : Fragment(R.layout.fragment_explore) {

    private var _binding: FragmentExploreBinding? = null
    private val binding get() = _binding!!

    private lateinit var placeAdapter: PlaceAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentExploreBinding.bind(view)

        // Setup main list of places for the current city
        val city = arguments?.getString("city") ?: "İstanbul"
        binding.toolbar.title = "$city Keşfet"

        placeAdapter = PlaceAdapter(PlaceRepository.getPlacesByCity(city)) { place ->
            val action = ExploreFragmentDirections.actionNavigationExploreToPlaceDetailFragment(place)
            findNavController().navigate(action)
        }
        binding.recyclerPlaces.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.recyclerPlaces.adapter = placeAdapter

        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}