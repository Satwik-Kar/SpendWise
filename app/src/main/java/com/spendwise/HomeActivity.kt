package com.spendwise


import android.app.Activity
import android.os.Bundle
import android.widget.LinearLayout

class HomeActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        var datas = listOf<Float>(0f, 200f, 300f, 500f, 300f, 100f)
        val barView = BarChartView(applicationContext, datas)

        val barChartLayout = findViewById<LinearLayout>(R.id.linearLayoutBarChart)

        barChartLayout.addView(barView)


    }
}