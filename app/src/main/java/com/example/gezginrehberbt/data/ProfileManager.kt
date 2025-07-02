package com.example.gezginrehberbt.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

object ProfileManager {

    private val _username = MutableLiveData("Atakan Demircioğlu")
    val username: LiveData<String> = _username

    private val _email = MutableLiveData("atakan.d@example.com")
    val email: LiveData<String> = _email

    fun updateUsername(newName: String) {
        _username.value = newName
    }
}
