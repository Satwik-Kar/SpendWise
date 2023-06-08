package com.spendwise


import android.animation.ValueAnimator
import android.app.Activity
import android.os.Bundle
import android.widget.LinearLayout


class HomeActivity : Activity() {
    lateinit var barLinearLayout: LinearLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        barLinearLayout = findViewById(R.id.barLinearLayout)
        val animatedView = BarChartView(this, arrayListOf<Float>(100f,200f,300f,400f,500f),
            arrayListOf("s","s","s","s","s")
        )
        barLinearLayout.addView(animatedView)



    }
}