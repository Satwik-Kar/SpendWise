package com.spendwise

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

class OpenCredit : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_open_credit)
        val toolbar = this.findViewById<Toolbar>(R.id.openCreditToolbar)
        toolbar.subtitle = "Credit Details"
        toolbar.setSubtitleTextColor(Color.WHITE)
        toolbar.setTitleTextColor(Color.WHITE)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        toolbar.setNavigationIcon(R.drawable.baseline_arrow_back_ios_24)

    }
}