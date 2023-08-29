package com.spendwise

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.material.textfield.TextInputEditText
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class OpenCredit : AppCompatActivity() {

    lateinit var title: String
    lateinit var sign: String
    lateinit var date: String
    lateinit var due_date: String
    lateinit var desc: String
    lateinit var amount: String
    lateinit var id: String
    lateinit var roi: String


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
        title = intent.getStringExtra("title").toString()
        sign = intent.getStringExtra("sign").toString()
        date = intent.getStringExtra("date").toString()
        due_date = intent.getStringExtra("due_date").toString()
        desc = intent.getStringExtra("desc").toString()
        amount = intent.getStringExtra("amount").toString()
        id = intent.getStringExtra("id").toString()
        roi = intent.getStringExtra("roi").toString()

        this.findViewById<TextView>(R.id.openCredit_title).text = title
        this.findViewById<TextView>(R.id.openCredit_date).text = date
        this.findViewById<TextView>(R.id.openCredit_duedate).text = due_date
        this.findViewById<TextView>(R.id.openCredit_roi).text = roi + " %"
//        this.findViewById<TextView>(R.id.openCredit_category).text = pMethod
        this.findViewById<TextView>(R.id.openCredit_Desc).text = desc
        this.findViewById<TextView>(R.id.openCredit_amount).text = "Credit Amount • $sign $amount"
        val tenure = findTenure(pickDate(date.toString()), pickDate(due_date.toString()))
        val total =
            calculateTotalAfterInterest(amount.toDouble(), roi.toDouble(), tenure.toDouble())
        this.findViewById<TextView>(R.id.tenure).text = "Tenure • $tenure Month(s)"

        this.findViewById<TextView>(R.id.totalAmt).text =
            "Total Amount to be paid after interest • $sign ${total.toInt()}"


    }

    fun calculateTotalAfterInterest(principal: Double, ROI: Double, tenure: Double): Double {


        return principal + (principal * (ROI / 100) * (tenure / 12))


    }

    fun findTenure(d1: LocalDate, d2: LocalDate): Long {
        return (ChronoUnit.MONTHS.between(d1, d2))

    }

    fun pickDate(dateTime: String): LocalDate {
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy  HH:mm:ss")
        val date = LocalDateTime.parse(dateTime, formatter)
        val fDate = date.toLocalDate()
        return fDate
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.expense_details_menu_items, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val idItem = item.itemId
        if (idItem == R.id.action_delete) {
            val alert = AlertDialog.Builder(this@OpenCredit)
            alert.setPositiveButton("Delete") { _, _ ->
                val email =
                    getSharedPreferences("credentials", MODE_PRIVATE).getString("email", null)!!
                val database = DatabaseHelper(this, email)
                try {

                    database.deleteCreditDataById(id)

                    val intent = Intent(this@OpenCredit, HomeActivity::class.java)
                    startActivity(intent)
                    finishAffinity()
                    Toast.makeText(this@OpenCredit, "Deleted", Toast.LENGTH_LONG).show()

                } catch (e: Exception) {
                    Log.e("delete", "onCreate: cannot delete data" + e.message)
                }


            }
            alert.setNegativeButton("No") { _, _ ->

                Toast.makeText(this@OpenCredit, "Delete Cancelled", Toast.LENGTH_LONG).show()
            }
            alert.setMessage("Are you sure to delete this credit?")
            alert.setTitle("Delete?")
            alert.show()
            return true
        } else if (idItem == R.id.action_edit) {
            val alert = AlertDialog.Builder(this@OpenCredit)
            alert.setTitle("Edit")
            alert.setMessage("Edit the details and click OK to save.")
            val view = layoutInflater.inflate(R.layout.edit_view, null)
            alert.setView(view)
            val titleInner = view.findViewById<TextInputEditText>(R.id.expenseEditTitle)
            val amountInner = view.findViewById<TextInputEditText>(R.id.expenseEditAmount)
            val descInner = view.findViewById<TextInputEditText>(R.id.expenseEditDesc)
            titleInner.setText(title)
            amountInner.setText(amount)
            descInner.setText(desc)
            alert.setPositiveButton("Ok") { _, _ ->

                if (titleInner.text!!.isNotEmpty() && amountInner.text!!.isNotEmpty() && descInner.text!!.isNotEmpty()) {
                    val email =
                        getSharedPreferences("credentials", MODE_PRIVATE).getString("email", null)!!
                    val database = DatabaseHelper(this@OpenCredit, email)
                    database.updateExpenseData(
                        id,
                        titleInner.text.toString(),
                        amountInner.text.toString(),
                        descInner.text.toString()
                    )
                    startActivity(Intent(this@OpenCredit, HomeActivity::class.java))
                    finishAffinity()
                    Toast.makeText(this@OpenCredit, "Edited Successfully", Toast.LENGTH_SHORT)
                        .show()
                } else {

                    Toast.makeText(
                        this@OpenCredit, "Fill up the required fields.", Toast.LENGTH_SHORT
                    ).show()

                }


            }
            alert.setNegativeButton("Cancel") { _, _ ->
                Toast.makeText(this@OpenCredit, "Cancelled", Toast.LENGTH_SHORT).show()

            }
            alert.show()

            return true
        }
        return super.onOptionsItemSelected(item)
    }
}