package com.spendwise

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView

class OpenExpense : AppCompatActivity() {

    lateinit var title:String
    lateinit var date:String
    lateinit var category:String
    lateinit var pMethod:String
    lateinit var desc:String
    lateinit var amount:String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_open_expense)
        title = intent.getStringExtra("title").toString()
        date = intent.getStringExtra("date").toString()
        category = intent.getStringExtra("category").toString()
        pMethod = intent.getStringExtra("p_method").toString()
        desc = intent.getStringExtra("desc").toString()
        amount = intent.getStringExtra("amount").toString()
        val firstViewElement = layoutInflater.inflate(R.layout.open_expense_first_element,null)
        val linearLayout = this.findViewById<LinearLayout>(R.id.openExpenseLinearLayout)
        firstViewElement.findViewById<TextView>(R.id.openExpense_title).text = title
        firstViewElement.findViewById<TextView>(R.id.openExpense_date).text = date
        firstViewElement.findViewById<TextView>(R.id.openExpense_category).text = category
        firstViewElement.findViewById<TextView>(R.id.openExpense_p_method).text = pMethod
        firstViewElement.findViewById<TextView>(R.id.openExpense_Desc).text = desc
        firstViewElement.findViewById<TextView>(R.id.openExpense_amount).text = amount
        linearLayout.addView(firstViewElement)
    }
}