package com.spendwise


import android.app.Activity
import android.os.Bundle
import android.widget.LinearLayout
import androidx.cardview.widget.CardView

class HomeActivity : Activity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        val bar1 = findViewById<CardView>(R.id.bar1)
        val bar2 = findViewById<CardView>(R.id.bar2)
        val bar3 = findViewById<CardView>(R.id.bar3)
        val bar4 = findViewById<CardView>(R.id.bar4)
        val bar5 = findViewById<CardView>(R.id.bar5)
        bar1.minimumHeight = 50
        bar2.minimumHeight = 25


    }
}