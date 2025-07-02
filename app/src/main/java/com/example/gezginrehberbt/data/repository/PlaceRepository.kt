package com.example.gezginrehberbt.data.repository

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.example.gezginrehberbt.R
import com.example.gezginrehberbt.model.Place
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * Uygulama genelinde mekan verilerini yöneten singleton repository.
 */
class PlaceRepository private constructor(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("gezgin_rehber_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()
    private val auth = FirebaseAuth.getInstance()

    private val FAVORITES_KEY_PREFIX = "favorites_"

    // Singleton instance
    companion object {
        @Volatile
        private var instance: PlaceRepository? = null
        
        // Tüm uygulamada kullanılacak mekan listesi
        private val _places = listOf(
            // Ankara
            Place(
                id = "anitkabir",
                name = "Anıtkabir",
                imageResId = R.drawable.yeni_anitkabir, // Thumbnail
                detailImageResId = R.drawable.yeni_anitkabir, // Same as thumbnail for now
                address = "Anıttepe, Ankara",
                distance = "2.1 km",
                rating = 4.9,
                category = "Anıt",
                latitude = 39.9255,
                longitude = 32.8369,
                city = "Ankara"
            ),
            Place(
                id = "atakule",
                name = "Atakule",
                imageResId = R.drawable.yeni_atakule, // Thumbnail
                detailImageResId = R.drawable.yeni_atakule, // Same as thumbnail for now
                address = "Çankaya, Ankara",
                distance = "5.3 km",
                rating = 4.4,
                category = "Gözlem Kulesi",
                latitude = 39.8896,
                longitude = 32.8552,
                city = "Ankara"
            ),
            // İzmir
            Place(
                id = "tarihi_asansor",
                name = "Tarihi Asansör",
                imageResId = R.drawable.yeni_tarihiasansor, // Thumbnail
                detailImageResId = R.drawable.yeni_tarihiasansor, // Same as thumbnail for now
                address = "Karataş, İzmir",
                distance = "1.5 km",
                rating = 4.6,
                category = "Tarihi Yapı",
                latitude = 38.4147,
                longitude = 27.1287,
                city = "İzmir"
            ),
            Place(
                id = "efes_antik_kent",
                name = "Efes Antik Kenti",
                imageResId = R.drawable.yeni_efesantikkent, // Thumbnail
                detailImageResId = R.drawable.yeni_efesantikkent, // Same as thumbnail for now
                address = "Selçuk, İzmir",
                distance = "74 km",
                rating = 4.8,
                category = "Antik Kent",
                latitude = 37.941,
                longitude = 27.342,
                city = "İzmir"
            ),
            // İstanbul
            Place(
                id = "galata_kulesi",
                name = "Galata Kulesi",
                imageResId = R.drawable.yeni_galata, // Thumbnail
                detailImageResId = R.drawable.yeni_galata, // Same as thumbnail for now
                address = "Galata, İstanbul",
                distance = "1.2 km",
                rating = 4.7,
                category = "Tarihi Kule",
                latitude = 41.0259,
                longitude = 28.9744,
                city = "İstanbul"
            ),
            Place(
                id = "ayasofya",
                name = "Ayasofya",
                imageResId = R.drawable.yeni_ayasofya, // Thumbnail
                detailImageResId = R.drawable.yeni_ayasofya, // Same as thumbnail for now
                address = "Sultanahmet, İstanbul",
                distance = "0.5 km",
                rating = 4.8,
                category = "Cami",
                latitude = 41.0086,
                longitude = 28.9802,
                city = "İstanbul"
            ),
            Place(
                id = "kapalicarsi",
                name = "Kapalıçarşı",
                imageResId = R.drawable.yeni_kapalicarsi, // Thumbnail
                detailImageResId = R.drawable.yeni_kapalicarsi, // Same as thumbnail for now
                address = "Beyazıt, İstanbul",
                distance = "0.8 km",
                rating = 4.5,
                category = "Tarihi Çarşı",
                latitude = 41.0105,
                longitude = 28.9682,
                city = "İstanbul"
            ),
            Place(
                id = "camlica_kulesi",
                name = "Çamlıca Kulesi",
                imageResId = R.drawable.yeni_camlicakulesi, // Thumbnail
                detailImageResId = R.drawable.yeni_camlicakulesi, // Same as thumbnail for now
                address = "Üsküdar, İstanbul",
                distance = "15 km",
                rating = 4.7,
                category = "Gözlem Kulesi", // Same as Atakule
                latitude = 41.0343,
                longitude = 29.0624,
                city = "İstanbul"
            ),
            Place(
                id = "cumhuriyet_aniti",
                name = "Cumhuriyet Anıtı",
                imageResId = R.drawable.yeni_cumhuriyetaniti, // Thumbnail
                detailImageResId = R.drawable.yeni_cumhuriyetaniti, // Same as thumbnail for now
                address = "Taksim, İstanbul",
                distance = "2.5 km",
                rating = 4.6,
                category = "Anıt",
                latitude = 41.0370,
                longitude = 28.9850,
                city = "İstanbul"
            )
        )

        fun getInstance(context: Context): PlaceRepository {
            return instance ?: synchronized(this) {
                instance ?: PlaceRepository(context.applicationContext).also { instance = it }
            }
        }
        
        // Doğrudan sınıf üzerinden çağrılabilmesi için companion object'e taşındı
        fun getPlacesByCity(city: String): List<Place> {
            return _places.filter { it.city == city }
        }
        
        // Doğrudan sınıf üzerinden çağrılabilmesi için companion object'e taşındı
        fun getPlaceById(id: String): Place? {
            return _places.find { it.id == id }
        }
        
        // Doğrudan sınıf üzerinden çağrılabilmesi için companion object'e taşındı
        fun getRecommendations(basePlace: Place, favorites: List<Place>): List<Place> {
            val favoriteNames = favorites.map { it.name }.toSet()
            return _places.filter {
                it.city == basePlace.city &&
                it.category == basePlace.category &&
                it.name != basePlace.name &&
                !favoriteNames.contains(it.name)
            }.take(2) // Sadece 2 tane öneri gösterelim
        }
    }

    val places: List<Place> = _places

    // Favori işlemleri
    fun addFavorite(placeId: String) {
        val favorites = getFavorites().toMutableSet()
        favorites.add(placeId)
        saveFavorites(favorites)
    }

    fun removeFavorite(placeId: String) {
        val favorites = getFavorites().toMutableSet()
        favorites.remove(placeId)
        saveFavorites(favorites)
    }

    fun isFavorite(placeId: String): Boolean {
        return getFavorites().contains(placeId)
    }

    fun getFavoritePlaces(): List<Place> {
        return getFavorites().mapNotNull { placeId -> getPlaceById(placeId) }
    }

    private fun getFavorites(): Set<String> {
        // Kullanıcı ID'sine göre favori anahtarını oluştur
        val userId = auth.currentUser?.uid ?: "anonymous"
        val key = FAVORITES_KEY_PREFIX + userId
        
        val json = sharedPreferences.getString(key, null) ?: return emptySet()
        val type = object : TypeToken<Set<String>>() {}.type
        return gson.fromJson(json, type) ?: emptySet()
    }

    private fun saveFavorites(favorites: Set<String>) {
        // Kullanıcı ID'sine göre favori anahtarını oluştur
        val userId = auth.currentUser?.uid ?: "anonymous"
        val key = FAVORITES_KEY_PREFIX + userId
        
        sharedPreferences.edit {
            putString(key, gson.toJson(favorites))
            apply()
        }
    }
    
    // Tüm kullanıcı favorilerini temizle (hesaptan çıkış durumunda)
    fun clearAllFavorites() {
        // Sadece mevcut kullanıcının favorilerini temizle
        val userId = auth.currentUser?.uid ?: "anonymous"
        val key = FAVORITES_KEY_PREFIX + userId
        
        sharedPreferences.edit {
            remove(key)
            apply()
        }
    }
}
