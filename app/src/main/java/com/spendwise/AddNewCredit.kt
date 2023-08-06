package com.spendwise

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import java.util.Calendar

class AddNewCredit : AppCompatActivity() {
    private val calendar = Calendar.getInstance()
    lateinit var creditTitle: TextInputEditText
    lateinit var creditAmount: TextInputEditText
    lateinit var creditDateTaken: EditText
    lateinit var creditDueDate: EditText
    lateinit var creditDescription: TextInputEditText
    lateinit var addCreditBtn: Button
    lateinit var datebtn: FloatingActionButton
    lateinit var dueDateBtn: FloatingActionButton


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_credit_edit_view)

        creditTitle = this.findViewById(R.id.credit_Title_editText)
        creditAmount = this.findViewById(R.id.creditAmount)
        creditDescription = this.findViewById(R.id.creditDesc)
        addCreditBtn = this.findViewById(R.id.addCreditBtn)
        creditDateTaken = this.findViewById(R.id.credit__date)
        creditDueDate = this.findViewById(R.id.credit_due_date)
        datebtn = this.findViewById(R.id.creditShowCalendarBtn)
        dueDateBtn = this.findViewById(R.id.creditShowDueDateBtn)


        datebtn.setOnClickListener {

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
                val database = DatabaseHelper(this@AddNewCredit, email)

                database.insertData(
                    email,
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
                val database = DatabaseHelper(this@AddNewCredit, email)

                database.insertData(
                    email,
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