package com.spendwise

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import java.util.Calendar

class AddNewCredit : AppCompatActivity() {
    private val calendar = Calendar.getInstance()
    lateinit var creditTitle: TextInputEditText
    lateinit var creditAmount: TextInputEditText
    lateinit var creditDateTaken: EditText
    lateinit var creditBillName: EditText
    lateinit var creditDescription: TextInputEditText
    lateinit var addCreditBtn: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_credit_edit_view)

        creditTitle = this.findViewById(R.id.credit_Title_editText)
        creditAmount = this.findViewById(R.id.creditAmount)
        creditDescription = this.findViewById(R.id.creditDesc)
        addCreditBtn = this.findViewById(R.id.addCreditBtn)
        creditDateTaken = this.findViewById(R.id.credit__date)
        creditBillName = this.findViewById(R.id.credit_reciept)

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

    private fun showDatePickerDialog() {
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
                creditDateTaken.setText(selectedDate)
            }, year, month, day
        )

        datePickerDialog.show()
    }


}