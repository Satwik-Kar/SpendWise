package com.spendwise

import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.DatePicker
import android.widget.EditText
import android.widget.LinearLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton

import java.util.Calendar

class AddNew : AppCompatActivity() {
    private val calendar = Calendar.getInstance()
    lateinit var detailTitle:EditText
    lateinit var detailDate:EditText
    lateinit var showCalendarbtn: FloatingActionButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_new)
        val view = layoutInflater.inflate(R.layout.detailsview, null)
        val addNewLinearLayout = findViewById<LinearLayout>(R.id.expenseDetails)
        addNewLinearLayout.addView(view)
        detailTitle = addNewLinearLayout.findViewById<EditText>(R.id.expenseDetails_title)
        detailDate = addNewLinearLayout.findViewById<EditText>(R.id.expenseDetails_date)
        showCalendarbtn =  addNewLinearLayout.findViewById<FloatingActionButton>(R.id.showCalendarBtn)
        showCalendarbtn.setOnClickListener {
            showDatePickerDialog()
        }


    }

    private fun showDatePickerDialog() {
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this, { _: DatePicker, selectedYear: Int, selectedMonth: Int, selectedDay: Int ->
                val selectedDate = formatDate(selectedYear, selectedMonth, selectedDay)
                detailDate.setText(selectedDate)
            },
            year,
            month,
            day
        )

        datePickerDialog.show()
    }

    private fun formatDate(year: Int, month: Int, day: Int): String {
        val formattedMonth = if (month + 1 < 10) "0${month + 1}" else "${month + 1}"
        val formattedDay = if (day < 10) "0$day" else "$day"
        return "$formattedDay/$formattedMonth/$year"
    }
}