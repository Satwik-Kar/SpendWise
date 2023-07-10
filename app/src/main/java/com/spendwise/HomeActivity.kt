package com.spendwise


import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.core.net.toUri
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
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
    private lateinit var filePaths: ArrayList<String>

    lateinit var addNewBtn: FloatingActionButton
    lateinit var recyclerViewListExpenses: RecyclerView
    private val COLUMN_TITLE = "title"
    private val COLUMN_DATE = "date"
    private val COLUMN_CATEGORY = "category"
    private val COLUMN_P_METHOD = "p_method"
    private val COLUMN_AMOUNT = "amount"
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
        val name = getSharedPreferences("credentials", MODE_PRIVATE).getString("name", "Sir")!!
        val firstName = name.split(" ")
        firstElementHome.findViewById<TextView>(R.id.homeWelcomeName).text =
            "${getWishes()} ${firstName[0]}"
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
        filePaths = ArrayList()
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
                    val description = it.getString(it.getColumnIndex(COLUMN_DESCRIPTION))

                    ids.add(id)
                    titles.add(title)
                    dates.add(date)
                    categories.add(category)
                    pMethods.add(pMethod)
                    amounts.add(amount)
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
        val accountPicture = firstElementHome.findViewById<ImageView>(R.id.accountPicture)
        val view = layoutInflater.inflate(R.layout.home_alert_profile_view, null)
        val alertDialog = AlertDialog.Builder(this, R.style.CustomAlertDialogStyle)
        accountPicture.setOnClickListener {
            val name = getSharedPreferences("credentials", MODE_PRIVATE).getString("name", "N/A")
            val email = getSharedPreferences("credentials", MODE_PRIVATE).getString("email", "N/A")
            val imgUri =
                getSharedPreferences("credentials", MODE_PRIVATE).getString("photo_url", "N/A")
            val country = getSharedPreferences("credentials", MODE_PRIVATE).getString(
                "country", "Default Country • N/A"
            )
            val imageView = view.findViewById<ImageView>(R.id.profile_image)
            val nameView = view.findViewById<TextView>(R.id.profile_display_name)
            val emailView = view.findViewById<TextView>(R.id.profile_email)
            val countryView = view.findViewById<TextView>(R.id.profile_country)
            val settingView = view.findViewById<CardView>(R.id.profile_setting)
            val logOutView = view.findViewById<CardView>(R.id.profile_log)
            val versionTextView = view.findViewById<TextView>(R.id.versionText)

            val parent = view.parent as? ViewGroup
            parent?.removeView(view)
            alertDialog.setView(view)
            val requestOptions: RequestOptions = RequestOptions.circleCropTransform()
            if (imgUri != "N/A") {
                Glide.with(this@HomeActivity).load(imgUri).apply(requestOptions).into(imageView)
            } else {
                Glide.with(this@HomeActivity).load(R.drawable.baseline_account_circle_24)
                    .apply(requestOptions).into(imageView)
            }
            countryView.text = "Default Country • $country"
            nameView.text = name
            emailView.text = email
            val packageInfo = packageManager.getPackageInfo(packageName, 0)
            versionTextView.text = getString(R.string.app_name) + " • v" + packageInfo.versionName

            alertDialog.create()
            alertDialog.show()
            settingView.setOnClickListener {

                val intent = Intent(this@HomeActivity, Settings::class.java)
                startActivity(intent)
            }
            logOutView.setOnClickListener {
                val alert = AlertDialog.Builder(this@HomeActivity)
                alert.setTitle("Log out").setMessage("Are you sure to log out?")
                alert.setPositiveButton(
                    "Log out"


                ) { _, _ ->
                    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestEmail()
                        .build()
                    val googleSignInClient = GoogleSignIn.getClient(this, gso)
                    googleSignInClient.signOut()
                    getSharedPreferences("credentials", MODE_PRIVATE).edit()
                        .putBoolean("hasAccountLoggedIn", false).apply()
                    finishAffinity()
                    Toast.makeText(this@HomeActivity, "Logged Out", Toast.LENGTH_LONG).show()


                }
                alert.setNegativeButton("No") { _, _ ->

                    Toast.makeText(
                        this@HomeActivity, "Logging out cancelled", Toast.LENGTH_SHORT
                    ).show()

                }
                alert.create().show()


            }


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
            intent.type = "new"
            startActivity(intent)
        }


    }

    private fun getWishes(): String {
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
            holder.imageView.setImageResource(R.drawable.baseline_auto_awesome_24)
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
        val imageView: ImageView = itemView.findViewById(R.id.imageView2)

    }
}