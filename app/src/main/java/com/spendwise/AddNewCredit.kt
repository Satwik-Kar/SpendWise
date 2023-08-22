package com.spendwise

import android.app.DatePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import java.util.Calendar

class AddNewCredit : AppCompatActivity() {
    private val calendar = Calendar.getInstance()
    private lateinit var creditTitle: TextInputEditText
    private lateinit var creditAmount: TextInputEditText
    private lateinit var creditDateTaken: EditText
    private lateinit var creditDueDate: EditText
    private lateinit var creditDescription: TextInputEditText
    private lateinit var addCreditBtn: Button
    private lateinit var dateBtn: FloatingActionButton
    private lateinit var dueDateBtn: FloatingActionButton
    private lateinit var rateOfInterestText: TextView
    private lateinit var rateOfInterestSlider: SeekBar
    var RATE_OF_INTEREST = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_credit_edit_view)
        val toolbar = this.findViewById<Toolbar>(R.id.addNewCreditToolbar)
        toolbar.subtitle = "Add a credit"
        toolbar.setSubtitleTextColor(Color.WHITE)
        toolbar.setTitleTextColor(Color.WHITE)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        toolbar.setNavigationIcon(R.drawable.baseline_arrow_back_ios_24)
        creditTitle = this.findViewById(R.id.credit_Title_editText)
        creditAmount = this.findViewById(R.id.creditAmount)
        creditDescription = this.findViewById(R.id.creditDesc)
        addCreditBtn = this.findViewById(R.id.addCreditBtn)
        creditDateTaken = this.findViewById(R.id.credit__date)
        creditDueDate = this.findViewById(R.id.credit_due_date)
        dateBtn = this.findViewById(R.id.creditShowCalendarBtn)
        dueDateBtn = this.findViewById(R.id.creditShowDueDateBtn)

        rateOfInterestText = this.findViewById(R.id.rateOfInterest)
        rateOfInterestSlider = this.findViewById(R.id.rateOfInterestSlider)


        rateOfInterestSlider.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                rateOfInterestText.text = "Rate of interest • $p1 %"
                RATE_OF_INTEREST = p1
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {

            }

            override fun onStopTrackingTouch(p0: SeekBar?) {

            }
        })

        dateBtn.setOnClickListener {

            showDatePickerDialog("date")


        }
        dueDateBtn.setOnClickListener {

            showDatePickerDialog("due_date")


        }
        addCreditBtn.setOnClickListener {

            if (creditTitle.text!!.isNotEmpty() && creditDateTaken.text.isNotEmpty()
                && creditAmount.text!!.isNotEmpty() && creditDueDate.text.isNotEmpty()
            ) {

                val email =
                    getSharedPreferences("credentials", MODE_PRIVATE).getString("email", null)!!
                val sign =
                    getSharedPreferences("credentials", MODE_PRIVATE).getString("currency", null)!!
                val database = DatabaseHelper(this@AddNewCredit, email)

                database.insertDataCredit(
                    RATE_OF_INTEREST.toString(),
                    email, sign,
                    creditTitle.text.toString(),
                    creditDateTaken.text.toString(),
                    creditAmount.text.toString(),
                    creditDueDate.text.toString()
                )
                Toast.makeText(
                    this@AddNewCredit, "added", Toast.LENGTH_SHORT
                ).show()
            } else if (creditTitle.text!!.isNotEmpty() && creditDateTaken.text.isNotEmpty()
                && creditAmount.text!!.isNotEmpty() && creditDueDate.text.isNotEmpty() && creditDescription.text!!.isNotEmpty()
            ) {
                val email =
                    getSharedPreferences("credentials", MODE_PRIVATE).getString("email", null)!!
                val sign =
                    getSharedPreferences("credentials", MODE_PRIVATE).getString("currency", null)!!

                val database = DatabaseHelper(this@AddNewCredit, email)

                database.insertDataCredit(
                    RATE_OF_INTEREST.toString(),
                    email, sign,
                    creditTitle.text.toString(),
                    creditDateTaken.text.toString(),
                    creditAmount.text.toString(),
                    creditDueDate.text.toString(),
                    creditDescription.text.toString()
                )

            } else {
                Toast.makeText(
                    this@AddNewCredit,
                    "Please fill the required fields",
                    Toast.LENGTH_SHORT
                ).show()
            }


        }
    }

    private fun formatDate(
        year: Int,
        month: Int,
        day: Int,
        hrs: Int,
        mins: Int,
        secs: Int
    ): String {
        val formattedMonth = if (month + 1 < 10) "0${month + 1}" else "${month + 1}"
        val formattedDay = if (day < 10) "0$day" else "$day"
        return "$formattedDay/$formattedMonth/$year  $hrs:$mins:$secs"
    }

    private fun showDatePickerDialog(which: String) {
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val hrs = calendar.get(Calendar.HOUR_OF_DAY)
        val mins = calendar.get(Calendar.MINUTE)
        val secs = calendar.get(Calendar.SECOND)

        val datePickerDialog = DatePickerDialog(
            this, { _: DatePicker, selectedYear: Int, selectedMonth: Int, selectedDay: Int ->
                val selectedDate =
                    formatDate(selectedYear, selectedMonth, selectedDay, hrs, mins, secs)
                if (which == "date") {
                    creditDateTaken.setText(selectedDate)
                } else if (which == "due_date") {
                    creditDueDate.setText(selectedDate)
                }

            }, year, month, day
        )

        datePickerDialog.show()
    }

    private fun removeDotsAndNumbers(email: String): String {
        val pattern = Regex("[.0-9@]")
        return pattern.replace(email, "")
    }

}