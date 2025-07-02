package com.example.gezginrehberbt

import android.app.Application
import com.example.gezginrehberbt.BuildConfig
import com.google.firebase.FirebaseApp
import com.facebook.stetho.Stetho
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class GezginRehberApp : Application() {
    
    override fun onCreate() {
        super.onCreate()
        
        // Firebase başlatılıyor
        FirebaseApp.initializeApp(this)
        
        // Stetho başlatılıyor
        if (BuildConfig.DEBUG) {
            Stetho.initializeWithDefaults(this)
        }
    }
    
    companion object {
        const val TAG = "GezginRehberApp"
    }
}
