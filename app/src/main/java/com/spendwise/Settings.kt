package com.spendwise

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat

class Settings : AppCompatActivity() {
    private lateinit var switch: SwitchCompat
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        switch = findViewById(R.id.switchAppLock)
        val stateOfAppLock = getSharedPreferences(
            "credentials", MODE_PRIVATE
        ).getBoolean("setting_app_lock", false)
        switch.isChecked = stateOfAppLock


        switch.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                // Switch is ON
                getSharedPreferences(
                    "credentials", MODE_PRIVATE
                ).edit().putBoolean("setting_app_lock", true).apply()
                Toast.makeText(this@Settings, "App Lock enabled", Toast.LENGTH_SHORT).show()

            } else {
                // Switch is OFF
                getSharedPreferences(
                    "credentials", MODE_PRIVATE
                ).edit().putBoolean("setting_app_lock", false).apply()
                Toast.makeText(this@Settings, "App Lock disabled", Toast.LENGTH_SHORT).show()

            }
        }


    }
}