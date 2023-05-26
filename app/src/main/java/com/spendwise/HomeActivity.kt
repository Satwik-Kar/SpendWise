package com.spendwise


import android.app.Activity
import android.os.Bundle
import android.widget.LinearLayout

class HomeActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        var barView = BarChartView(applicationContext)
        var barChartLayout = findViewById<LinearLayout>(R.id.linearLayoutBarChart)
        barChartLayout.addView(barView)


    }
}