package com.example.gezginrehberbt.data.repository

import com.example.gezginrehberbt.data.FavoritesManager
import com.example.gezginrehberbt.util.Result
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor() : AuthRepository {
    
    private val auth: FirebaseAuth = Firebase.auth
    
    override suspend fun login(email: String, password: String): Result<Unit> {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Authentication failed")
        }
    }

    override suspend fun register(email: String, password: String, name: String): Result<Unit> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            // Update user profile with name
            result.user?.updateProfile(
                com.google.firebase.auth.UserProfileChangeRequest.Builder()
                    .setDisplayName(name)
                    .build()
            )?.await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Registration failed")
        }
    }

    override suspend fun forgotPassword(email: String): Result<Unit> {
        return try {
            auth.sendPasswordResetEmail(email).await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to send password reset email")
        }
    }

    override fun logout() {
        auth.signOut()
        // Çıkış yapıldığında favorileri temizle
        FavoritesManager.clearFavorites()
    }

    override fun isLoggedIn(): Boolean {
        return auth.currentUser != null
    }
}
