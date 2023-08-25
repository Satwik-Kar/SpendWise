package com.spendwise

import android.graphics.Color
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

class OpenCredit : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_open_credit)
        val toolbar = this.findViewById<Toolbar>(R.id.openCreditToolbar)
        toolbar.subtitle = "Credit Details"
        toolbar.setSubtitleTextColor(Color.BLACK)
        toolbar.setTitleTextColor(Color.BLACK)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        toolbar.setNavigationIcon(R.drawable.baseline_arrow_back_ios_24)
        val title = intent.getStringExtra("title")
        val sign = intent.getStringExtra("sign")
        val date = intent.getStringExtra("date")
        val due_date = intent.getStringExtra("due_date")
        val desc = intent.getStringExtra("desc")
        val amount = intent.getStringExtra("amount")
        val id = intent.getStringExtra("id")
        val roi = intent.getStringExtra("roi")

        this.findViewById<TextView>(R.id.openCredit_title).text = title
        this.findViewById<TextView>(R.id.openCredit_date).text = date
        this.findViewById<TextView>(R.id.openCredit_duedate).text = due_date
        this.findViewById<TextView>(R.id.openCredit_roi).text = roi + " %"
//        this.findViewById<TextView>(R.id.openCredit_category).text = pMethod
        this.findViewById<TextView>(R.id.openCredit_Desc).text = desc
        this.findViewById<TextView>(R.id.openCredit_amount).text = "$sign $amount"


    }
}