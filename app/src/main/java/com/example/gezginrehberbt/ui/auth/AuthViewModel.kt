package com.example.gezginrehberbt.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gezginrehberbt.data.repository.AuthRepository
import com.example.gezginrehberbt.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _loginState = MutableLiveData<AuthState>()
    val loginState: LiveData<AuthState> = _loginState

    private val _registerState = MutableLiveData<AuthState>()
    val registerState: LiveData<AuthState> = _registerState

    fun login(email: String, password: String) {
        _loginState.value = AuthState.Loading
        
        viewModelScope.launch {
            try {
                val result = authRepository.login(email, password)
                if (result is Result.Success) {
                    _loginState.value = AuthState.Success
                } else if (result is Result.Error) {
                    _loginState.value = AuthState.Error(result.message ?: "An unknown error occurred")
                }
            } catch (e: Exception) {
                _loginState.value = AuthState.Error(e.message ?: "An unknown error occurred")
            }
        }
    }

    fun register(name: String, email: String, password: String) {
        _registerState.value = AuthState.Loading

        viewModelScope.launch {
            try {
                val result = authRepository.register(email, password, name)
                if (result is Result.Success) {
                    _registerState.value = AuthState.Success
                } else if (result is Result.Error) {
                    _registerState.value = AuthState.Error(result.message ?: "An unknown error occurred")
                }
            } catch (e: Exception) {
                _registerState.value = AuthState.Error(e.message ?: "An unknown error occurred")
            }
        }
    }

    sealed class AuthState {
        object Loading : AuthState()
        object Success : AuthState()
        data class Error(val message: String) : AuthState()
    }
}
