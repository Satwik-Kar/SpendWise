package com.spendwise

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler

class MainActivity : Activity() {
    private val SPLASH_TIMEOUT: Long = 2000
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Handler().postDelayed({
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }, SPLASH_TIMEOUT)


    }
}