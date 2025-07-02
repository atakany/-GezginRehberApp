package com.example.gezginrehberbt.data.repository

import com.example.gezginrehberbt.util.Result

interface AuthRepository {
    suspend fun login(email: String, password: String): Result<Unit>
    suspend fun register(email: String, password: String, name: String): Result<Unit>
    suspend fun forgotPassword(email: String): Result<Unit>
    fun logout()
    fun isLoggedIn(): Boolean
}
