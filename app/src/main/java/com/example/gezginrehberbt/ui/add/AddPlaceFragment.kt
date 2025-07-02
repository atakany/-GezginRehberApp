package com.example.gezginrehberbt.ui.add

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.example.gezginrehberbt.R
import com.example.gezginrehberbt.databinding.FragmentAddPlaceBinding
import com.google.android.material.snackbar.Snackbar

class AddPlaceFragment : Fragment(R.layout.fragment_add_place) {

    private var _binding: FragmentAddPlaceBinding? = null
    private val binding get() = _binding!!

    private val selectImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            binding.ivPlaceImage.setImageURI(it)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAddPlaceBinding.bind(view)

        binding.ivPlaceImage.setOnClickListener {
            selectImageLauncher.launch("image/*")
        }

        binding.btnSavePlace.setOnClickListener {
            hideKeyboard()
            Snackbar.make(it, "Mekan onaya gönderildi.", Snackbar.LENGTH_LONG).show()
            clearForm()
        }
    }

    private fun clearForm() {
        binding.etPlaceName.text?.clear()
        binding.etPlaceCategory.text?.clear()
        binding.etPlaceDescription.text?.clear()
        binding.ivPlaceImage.setImageResource(android.R.drawable.ic_menu_camera)
        binding.etPlaceName.requestFocus()
    }

    private fun hideKeyboard() {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.hideSoftInputFromWindow(view?.windowToken, 0)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
