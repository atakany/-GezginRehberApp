package com.example.gezginrehberbt.model

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

/**
 * Firestore'da saklanacak yorum ve puanlama verilerini temsil eden model sınıfı.
 *
 * @property id Benzersiz yorum kimliği.
 * @property placeId Yorumun yapıldığı mekanın kimliği.
 * @property userId Yorumu yapan kullanıcının kimliği.
 * @property userName Yorumu yapan kullanıcının adı.
 * @property rating Kullanıcının verdiği puan (1-5 arası).
 * @property text Kullanıcının yazdığı yorum metni.
 * @property timestamp Yorumun yapıldığı zaman damgası (Firebase tarafından otomatik atanır).
 */
data class Comment(
    val id: String = "",
    val placeId: String = "",
    val userId: String = "",
    val userName: String = "",
    val rating: Float = 0.0f,
    val text: String = "",
    @ServerTimestamp
    val timestamp: Date? = null
)
