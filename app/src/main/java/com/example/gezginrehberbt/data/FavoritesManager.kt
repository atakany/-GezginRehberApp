package com.example.gezginrehberbt.data

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.gezginrehberbt.data.repository.PlaceRepository
import com.example.gezginrehberbt.model.Place
import com.google.firebase.auth.FirebaseAuth

/**
 * Singleton object to manage favorite places.
 * Uses PlaceRepository for persistent storage.
 */
object FavoritesManager {

    private val _favorites = MutableLiveData<List<Place>>(emptyList())
    val favorites: LiveData<List<Place>> = _favorites
    
    private var repository: PlaceRepository? = null
    private var auth: FirebaseAuth? = null
    private var authStateListener: FirebaseAuth.AuthStateListener? = null
    
    fun initialize(context: Context) {
        repository = PlaceRepository.getInstance(context)
        
        // Firebase Auth instance'ını al
        auth = FirebaseAuth.getInstance()
        
        // Auth durumu değişikliklerini izle
        authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            // Kullanıcı değiştiğinde favorileri güncelle
            refreshFavorites()
        }
        
        // Auth state listener'ı kaydet
        auth?.addAuthStateListener(authStateListener!!)
        
        // İlk başlangıçta favorileri yükle
        refreshFavorites()
    }
    
    fun refreshFavorites() {
        repository?.let { repo ->
            _favorites.value = repo.getFavoritePlaces()
        }
    }

    fun toggleFavorite(place: Place) {
        repository?.let { repo ->
            if (isFavorite(place)) {
                repo.removeFavorite(place.id)
            } else {
                repo.addFavorite(place.id)
            }
            refreshFavorites()
        }
    }

    fun isFavorite(place: Place): Boolean {
        return repository?.isFavorite(place.id) ?: false
    }
    
    fun getRecommendedPlaces(): List<Place> {
        val favoritePlaces = _favorites.value ?: return emptyList()
        if (favoritePlaces.isEmpty()) return emptyList()
        
        val favoriteCities = favoritePlaces.map { it.city }.toSet()
        val favoriteIds = favoritePlaces.map { it.id }.toSet()
        val recommendations = mutableListOf<Place>()
        
        favoriteCities.forEach { city ->
            // Her şehir için favorilerde olmayan 1-2 mekan öner
            val cityPlaces = PlaceRepository.getPlacesByCity(city)
                .filter { it.id !in favoriteIds }
                .shuffled()
                .take(2)
            recommendations.addAll(cityPlaces)
        }
        
        return recommendations
    }
    
    // Kullanıcı çıkış yaptığında çağrılacak
    fun clearFavorites() {
        repository?.clearAllFavorites()
        refreshFavorites()
    }
    
    // Uygulama kapanırken AuthStateListener'ı kaldır
    fun cleanup() {
        auth?.removeAuthStateListener(authStateListener!!)
    }
}
