package com.example.gezginrehberbt.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Place(
    val id: String, 
    val name: String,
    val imageResId: Int, // Thumbnail image
    val detailImageResId: Int? = null, // Optional detail image
    val address: String,
    val distance: String,
    val rating: Double,
    val category: String,
    val latitude: Double,
    val longitude: Double,
    val city: String
) : Parcelable {
    // For backward compatibility
    fun getDetailImage(): Int = detailImageResId ?: imageResId
}
