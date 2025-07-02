package com.example.gezginrehberbt.ui.splash

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.gezginrehberbt.ui.auth.AuthActivity

/**
 * Simple splash screen that immediately routes the user to the authentication flow.
 * Replace the redirection logic when you implement onboarding or a better splash.
 */
class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // No layout; straight redirection
        startActivity(Intent(this, AuthActivity::class.java))
        finish()
    }
}
