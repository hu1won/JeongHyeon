package com.hu1won.jeonghyeon

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity

class Splash : AppCompatActivity() {
    var bool = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash)

        Handler().postDelayed({
            if (bool) {
                val intent = Intent(this@Splash, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }, 3000)
    }

    override fun onDestroy() {
        super.onDestroy()
        bool = false
    }
}