package com.dicoding.picodiploma.hugme.view.splashscreen

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.dicoding.picodiploma.hugme.R
import com.dicoding.picodiploma.hugme.data.pref.UserPreference
import com.dicoding.picodiploma.hugme.data.pref.dataStore
import com.dicoding.picodiploma.hugme.view.main.MainActivity
import com.dicoding.picodiploma.hugme.view.welcome.WelcomeActivity
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class SplashActivity : AppCompatActivity() {

    private val splashDelay: Long = 3000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                )
        supportActionBar?.hide()

        val userPreference = UserPreference.getInstance(dataStore)

        userPreference.isSessionSaved().onEach { isSessionSaved ->
            val intent = if (isSessionSaved) {
                Intent(this, MainActivity::class.java)
            } else {
                Intent(this, WelcomeActivity::class.java)
            }

            Handler(Looper.getMainLooper()).postDelayed({
                startActivity(intent)
                finish()
            }, splashDelay)
        }.launchIn(lifecycleScope)
    }
}
