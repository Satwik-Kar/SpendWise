package com.spendwise


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton


class HomeActivity : Activity() {
    lateinit var barLinearLayout: LinearLayout
    lateinit var homeLinearLayout: LinearLayout
    lateinit var titles: ArrayList<String>
    lateinit var dates: ArrayList<String>
    lateinit var amounts: ArrayList<String>
    lateinit var addNewBtn: FloatingActionButton
    lateinit var recyclerViewListExpenses: RecyclerView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        val firstElementHome = layoutInflater.inflate(R.layout.first_element_home,null)
        val secondElementHome = layoutInflater.inflate(R.layout.list_of_expenses,null)

        recyclerViewListExpenses = secondElementHome.findViewById(R.id.recyclerView_list_expenses)
        barLinearLayout = firstElementHome.findViewById(R.id.barLinearLayout)
        homeLinearLayout = this.findViewById(R.id.homeActivity_LinearLayout)
        titles = ArrayList()
        dates = ArrayList()
        amounts = ArrayList()
        titles.add("title")
        dates.add("date")
        amounts.add("amount")
        titles.add("title")
        dates.add("date")
        amounts.add("amount")
        titles.add("title")
        dates.add("date")
        amounts.add("amount")
        val dividerItemDecoration = DividerItemDecoration(recyclerViewListExpenses.context, LinearLayoutManager.VERTICAL)
        recyclerViewListExpenses.addItemDecoration(dividerItemDecoration)
        val adapter = ListAdapter(titles,dates, amounts)
        recyclerViewListExpenses.adapter = adapter
        recyclerViewListExpenses.layoutManager = LinearLayoutManager(this)
        homeLinearLayout.addView(firstElementHome)
        homeLinearLayout.addView(secondElementHome)

        addNewBtn = this.findViewById(R.id.addNew)
        val animatedView = BarChartView(this, arrayListOf(100f,200f,300f,400f,500f),
            arrayListOf("s","s","s","s","s")
        )
        barLinearLayout.addView(animatedView)
        addNewBtn.setOnClickListener {
            val intent = Intent(this@HomeActivity,AddNew::class.java)
            startActivity(intent)
        }



    }
    private inner class ListAdapter(var titles:ArrayList<String>,var dates:ArrayList<String>,var amounts:ArrayList<String>):RecyclerView.Adapter<ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.list_of_expenses_recycler_view, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {

           val titleView = holder.title
           val dateView = holder.date
           val amountView = holder.amount

            titleView.setText(titles[position])
            dateView.setText(dates[position])
            amountView.setText(amounts[position])



        }

        override fun getItemCount(): Int {
            return titles.size

        }
    }

    private inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val title = itemView.findViewById<TextView>(R.id.title_view)
        val date = itemView.findViewById<TextView>(R.id.date_view)
        val amount = itemView.findViewById<TextView>(R.id.amount_view)


    }
}