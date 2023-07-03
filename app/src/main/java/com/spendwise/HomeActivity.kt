package com.spendwise


import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.icu.util.Calendar
import android.opengl.Visibility
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.net.toUri
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton

class HomeActivity : Activity() {
    lateinit var barLinearLayout: LinearLayout
    lateinit var homeLinearLayout: LinearLayout

    private lateinit var titles: ArrayList<String>
    private lateinit var dates: ArrayList<String>
    private lateinit var categories: ArrayList<String>
    private lateinit var pMethods: ArrayList<String>
    private lateinit var reciepts: ArrayList<ByteArray>
    private lateinit var descriptions: ArrayList<String>
    private lateinit var ids: ArrayList<String>
    private lateinit var amounts: ArrayList<String>

    lateinit var addNewBtn: FloatingActionButton
    lateinit var recyclerViewListExpenses: RecyclerView
    private val TABLE_NAME = "Expenses"
    private val COLUMN_TITLE = "title"
    private val COLUMN_DATE = "date"
    private val COLUMN_CATEGORY = "category"
    private val COLUMN_P_METHOD = "p_method"
    private val COLUMN_AMOUNT = "amount"
    private val COLUMN_BLOB_RECEIPT = "BlobDataReceipt"
    private val COLUMN_DESCRIPTION = "description"

    @SuppressLint("Range", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        val firstElementHome = layoutInflater.inflate(R.layout.first_element_home, null)
        val secondElementHome = layoutInflater.inflate(R.layout.list_of_expenses, null)
        val uri = getSharedPreferences("credentials", MODE_PRIVATE).getString("photo_url", null)
        Log.e("uri", "onCreate: $uri")
        if (uri != null) {
            val requestOptions: RequestOptions = RequestOptions.circleCropTransform()

            Glide.with(this).load(uri.toUri()).apply(requestOptions)
                .into(firstElementHome.findViewById(R.id.accountPicture))

        } else {
            Log.e("account-photo", "onCreate: photo_url is null")
        }
        val name= getSharedPreferences("credentials", MODE_PRIVATE).getString("name","Sir")!!
        val firstName  =name.split(" ")
        firstElementHome.findViewById<TextView>(R.id.homeWelcomeName).text = "${getWishes()} ${firstName[0]}"
        recyclerViewListExpenses = secondElementHome.findViewById(R.id.recyclerView_list_expenses)
        barLinearLayout = firstElementHome.findViewById(R.id.barLinearLayout)
        homeLinearLayout = this.findViewById(R.id.homeActivity_LinearLayout)
        titles = ArrayList()
        dates = ArrayList()
        amounts = ArrayList()
        pMethods = ArrayList()
        categories = ArrayList()
        reciepts = ArrayList()
        descriptions = ArrayList()
        ids = ArrayList()

        try {
            val database = DatabaseHelper(this)
            val cursor = database.retrieveData()!!
            cursor.use {
                while (it.moveToNext()) {
                    val id = it.getString(it.getColumnIndex("_id"))
                    val title = it.getString(it.getColumnIndex(COLUMN_TITLE))
                    val date = it.getString(it.getColumnIndex(COLUMN_DATE))
                    val category = it.getString(it.getColumnIndex(COLUMN_CATEGORY))
                    val pMethod = it.getString(it.getColumnIndex(COLUMN_P_METHOD))
                    val amount = it.getString(it.getColumnIndex(COLUMN_AMOUNT))
                    val receipt = it.getBlob(it.getColumnIndex(COLUMN_BLOB_RECEIPT))
                    val description = it.getString(it.getColumnIndex(COLUMN_DESCRIPTION))
                    ids.add(id)
                    titles.add(title)
                    dates.add(date)
                    categories.add(category)
                    pMethods.add(pMethod)
                    amounts.add(amount)
                    reciepts.add(receipt)
                    descriptions.add(description)


                }
            }

        } catch (e: Exception) {
            Log.e("database", "onCreate: $e")
        }

        val dividerItemDecoration =
            DividerItemDecoration(recyclerViewListExpenses.context, LinearLayoutManager.VERTICAL)
        recyclerViewListExpenses.addItemDecoration(dividerItemDecoration)
        if (titles.isNotEmpty()) {
            val adapter = ListAdapter(titles, dates, categories, pMethods, descriptions, amounts)
            recyclerViewListExpenses.adapter = adapter
            recyclerViewListExpenses.layoutManager = LinearLayoutManager(this)

        } else {
            secondElementHome.findViewById<TextView>(R.id.noitemtext).visibility = View.VISIBLE
        }
        homeLinearLayout.addView(firstElementHome)
        homeLinearLayout.addView(secondElementHome)


        addNewBtn = this.findViewById(R.id.addNew)
        val animatedView = BarChartView(
            this, arrayListOf(100f, 200f, 300f, 400f, 500f), arrayListOf("s", "s", "s", "s", "s")
        )
        barLinearLayout.addView(animatedView)
        addNewBtn.setOnClickListener {
            val intent = Intent(this@HomeActivity, AddNew::class.java)
            startActivity(intent)
        }


    }
    fun getWishes(): String {
        val calendar = Calendar.getInstance()

        return when (calendar.get(Calendar.HOUR_OF_DAY)) {
            in 0..11 -> "Good morning!"
            in 12..15 -> "Good afternoon!"
            in 16..20 -> "Good evening!"
            else -> "Hello!"
        }
    }



    private inner class ListAdapter(
        var titles: ArrayList<String>,
        var dates: ArrayList<String>,
        var categories: ArrayList<String>,
        var p_methods: ArrayList<String>,
        var descriptions: ArrayList<String>,
        var amounts: ArrayList<String>
    ) : RecyclerView.Adapter<ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.list_of_expenses_recycler_view, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {

            val titleView = holder.title
            val dateView = holder.date
            val amountView = holder.amount
            val cardView = holder.cardView

            titleView.text = titles[position]
            dateView.text = dates[position]
            amountView.text = amounts[position]
            cardView.setOnClickListener {
                val intent = Intent(this@HomeActivity, OpenExpense::class.java)
                intent.putExtra("id", ids[position])
                intent.putExtra("title", titles[position])
                intent.putExtra("date", dates[position])
                intent.putExtra("category", categories[position])
                intent.putExtra("p_method", p_methods[position])
                intent.putExtra("desc", descriptions[position])
                intent.putExtra("amount", amounts[position])

                startActivity(intent)


            }


        }

        override fun getItemCount(): Int {
            return titles.size

        }
    }

    private inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.title_view)
        val date: TextView = itemView.findViewById(R.id.date_view)
        val amount: TextView = itemView.findViewById(R.id.amount_view)
        val cardView: CardView = itemView.findViewById(R.id.listExpenses_cardView)

    }
}