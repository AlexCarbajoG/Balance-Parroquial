package com.carbajo.checking.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.carbajo.checking.R
import com.google.firebase.auth.FirebaseAuth

class SplashActivity : AppCompatActivity() {

    private val handler = Handler(Looper.getMainLooper())

    private val abrirMain = Runnable{
        val destination = if (FirebaseAuth.getInstance().currentUser == null) LoginActivity::class.java else MainActivity::class.java
        val intent = Intent(this, destination)
        startActivity(intent)
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        handler.postDelayed(abrirMain, 4000)

    }

    override fun onDestroy() {
        handler.removeCallbacks(abrirMain)
        super.onDestroy()
    }

}
