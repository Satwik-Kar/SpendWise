package com.spendwise


import android.animation.ValueAnimator
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton


class HomeActivity : Activity() {
    lateinit var barLinearLayout: LinearLayout
    lateinit var homeLinearLayout: LinearLayout

    lateinit var addNewBtn: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        val view = layoutInflater.inflate(R.layout.first_element_home,null)
        barLinearLayout = view.findViewById(R.id.barLinearLayout)
        homeLinearLayout = this.findViewById(R.id.homeActivity_LinearLayout)
        homeLinearLayout.addView(view)
        addNewBtn = this.findViewById(R.id.addNew)
        val animatedView = BarChartView(this, arrayListOf<Float>(100f,200f,300f,400f,500f),
            arrayListOf("s","s","s","s","s")
        )
        barLinearLayout.addView(animatedView)
        addNewBtn.setOnClickListener {
            val intent = Intent(this@HomeActivity,AddNew::class.java)
            startActivity(intent)
        }



    }
}