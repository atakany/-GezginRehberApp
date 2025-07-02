package com.example.gezginrehberbt.ui.profile

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.gezginrehberbt.R
import com.example.gezginrehberbt.databinding.FragmentEditProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class EditProfileFragment : Fragment(R.layout.fragment_edit_profile) {

    private var _binding: FragmentEditProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentEditProfileBinding.bind(view)
        auth = Firebase.auth
        val currentUser = auth.currentUser

        binding.toolbar.setNavigationOnClickListener { findNavController().navigateUp() }

        binding.etUsername.setText(currentUser?.displayName)

        binding.btnSave.setOnClickListener {
            val newUsername = binding.etUsername.text.toString()
            if (newUsername.isNotBlank()) {
                val profileUpdates = UserProfileChangeRequest.Builder()
                    .setDisplayName(newUsername)
                    .build()

                currentUser?.updateProfile(profileUpdates)?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(requireContext(), "Profil güncellendi.", Toast.LENGTH_SHORT).show()
                        findNavController().navigateUp()
                    } else {
                        Toast.makeText(requireContext(), "Güncelleme başarısız oldu: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                    }
                }
            } else {
                binding.etUsername.error = "Kullanıcı adı boş olamaz."
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
