package com.spendwise

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.content.ContentResolver
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.Toast
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.io.ByteArrayOutputStream
import java.io.InputStream

import java.util.Calendar
import kotlin.properties.Delegates


/**
 * Add new.
 *
 * @constructor Create empty constructor for add new
 */
class AddNew : AppCompatActivity() {
    private val PICK_FILE_REQUEST_CODE = 1

    private val calendar = Calendar.getInstance()
    lateinit var detailTitle: EditText
    lateinit var detailDate: EditText
    lateinit var detailAmount: EditText
    lateinit var detailReceiptUri: EditText
    lateinit var detailDesc: EditText
    lateinit var detailCategory: Spinner
    lateinit var detailMethod: Spinner

    lateinit var showCalendarbtn: FloatingActionButton
    lateinit var showFilesbtn: FloatingActionButton
    lateinit var submitDetails: Button
    private lateinit var TITLE: String
    private lateinit var DATE: String
    private lateinit var CATEGORY: String
    private lateinit var P_METHOD: String
    private lateinit var AMOUNT: String
    private lateinit var RECIEPT: ByteArray
    private lateinit var DESCRIPTION: String
    private var hasReceipt = false

    /**
     * On create.
     *
     * @param savedInstanceState Saved instance state
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_new)
        val view = layoutInflater.inflate(R.layout.detailsview, null)
        val addNewLinearLayout = findViewById<LinearLayout>(R.id.expenseDetails)
        addNewLinearLayout.addView(view)
        detailTitle = addNewLinearLayout.findViewById<EditText>(R.id.expenseDetails_title)
        detailDate = addNewLinearLayout.findViewById<EditText>(R.id.expenseDetails_date)
        showCalendarbtn =
            addNewLinearLayout.findViewById<FloatingActionButton>(R.id.showCalendarBtn)
        showFilesbtn = addNewLinearLayout.findViewById<FloatingActionButton>(R.id.showFilesBtn)

        showCalendarbtn.setOnClickListener {
            showDatePickerDialog()
        }
        showFilesbtn.setOnClickListener {
            getFile()
        }
        detailCategory = addNewLinearLayout.findViewById(R.id.expenseDetails_spinner_category)
        detailMethod = addNewLinearLayout.findViewById(R.id.expenseDetails_spinner_payment_method)

        detailAmount = addNewLinearLayout.findViewById(R.id.expenseDetails_amount)
        detailReceiptUri = addNewLinearLayout.findViewById(R.id.expenseDetails_reciept)
        detailDesc = addNewLinearLayout.findViewById(R.id.expenseDetails_desc)
        submitDetails = findViewById(R.id.expenseDetails_submit)


        submitDetails.setOnClickListener {
            Log.e("database", "onCreate: pressed")
            TITLE = detailTitle.text.toString()
            DATE = detailDate.text.toString()
            CATEGORY = detailCategory.selectedItem.toString()
            P_METHOD = detailMethod.selectedItem.toString()
            AMOUNT = detailAmount.text.toString()
            DESCRIPTION = detailDesc.text.toString()

            if (TITLE.isNotEmpty() && DATE.isNotEmpty() && CATEGORY.isNotEmpty() && P_METHOD.isNotEmpty() && AMOUNT.isNotEmpty() && DESCRIPTION.isNotEmpty() && hasReceipt) {

                submit(hasDescription = true, hasReceipt = true)
            } else if (TITLE.isNotEmpty() && DATE.isNotEmpty() && CATEGORY.isNotEmpty() && P_METHOD.isNotEmpty() && AMOUNT.isNotEmpty() && DESCRIPTION.isEmpty() && !hasReceipt) {

                submit(hasDescription = false, hasReceipt = false)
            } else if (TITLE.isNotEmpty() && DATE.isNotEmpty() && CATEGORY.isNotEmpty() && P_METHOD.isNotEmpty() && AMOUNT.isNotEmpty() && DESCRIPTION.isEmpty() && hasReceipt) {

                submit(hasDescription = false, hasReceipt = true)
            } else if (TITLE.isNotEmpty() && DATE.isNotEmpty() && CATEGORY.isNotEmpty() && P_METHOD.isNotEmpty() && AMOUNT.isNotEmpty() && DESCRIPTION.isNotEmpty() && !hasReceipt) {

                submit(hasDescription = true, hasReceipt = false)
            } else {
                Toast.makeText(this@AddNew, "Please fill the empty fields.", Toast.LENGTH_LONG)
                    .show()
            }


        }

    }

    /**
     * Show date picker dialog.
     */
    private fun showDatePickerDialog() {
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this, { _: DatePicker, selectedYear: Int, selectedMonth: Int, selectedDay: Int ->
                val selectedDate = formatDate(selectedYear, selectedMonth, selectedDay)
                detailDate.setText(selectedDate)
            }, year, month, day
        )

        datePickerDialog.show()
    }

    /**
     * Format date.
     *
     * @param year Year
     * @param month Month
     * @param day Day
     * @return
     */
    private fun formatDate(year: Int, month: Int, day: Int): String {
        val formattedMonth = if (month + 1 < 10) "0${month + 1}" else "${month + 1}"
        val formattedDay = if (day < 10) "0$day" else "$day"
        return "$formattedDay/$formattedMonth/$year"
    }

    /**
     * Get file.
     */
    private fun getFile() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "*/*"
        }
        startActivityForResult(intent, PICK_FILE_REQUEST_CODE)
    }

    /**
     * Submit.
     *
     * @param hasDescription Has description
     * @param hasReceipt Has receipt
     */
    @SuppressLint("Range")
    private fun submit(hasDescription: Boolean, hasReceipt: Boolean) {
        val database = DatabaseHelper(this@AddNew)
        var a by Delegates.notNull<Int>()
        if (hasDescription && hasReceipt) {
            a = 1
        } else if (!hasDescription && hasReceipt) {
            a = 2
        } else if (hasDescription && !hasReceipt) {
            a = 3
        } else if (!hasReceipt && !hasDescription) {
            a = 4
        }

        when (a) {
            1 -> {
                database.insertData(TITLE, DATE, CATEGORY, P_METHOD, AMOUNT, RECIEPT, DESCRIPTION)

            }

            2 -> {
                database.insertData(TITLE, DATE, CATEGORY, P_METHOD, AMOUNT, RECIEPT)

            }

            3 -> {
                database.insertData(TITLE, DATE, CATEGORY, P_METHOD, AMOUNT, DESCRIPTION)
                Log.e("database", "successfullmine")
            }

            4 -> {
                database.insertData(TITLE, DATE, CATEGORY, P_METHOD, AMOUNT)

            }
        }
        val intent = Intent(this@AddNew,HomeActivity::class.java)
        startActivity(intent)
        finishAffinity()


    }

    /**
     * Handle photo selection.
     *
     * @param contentResolver Content resolver
     * @param uri Uri
     */
    private fun handlePhotoSelection(contentResolver: ContentResolver, uri: Uri) {

        try {
            val inputStream: InputStream? = contentResolver.openInputStream(uri)
            val displayName = getFileName(contentResolver, uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            val outputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            val byteArray = outputStream.toByteArray()
            RECIEPT = byteArray
            hasReceipt = true
            detailReceiptUri.setText(displayName)

            inputStream?.close()
        } catch (e: Exception) {
            hasReceipt = false
            e.printStackTrace()
        }
    }

    /**
     * Get file name.
     *
     * @param contentResolver Content resolver
     * @param uri Uri
     * @return
     */
    @SuppressLint("Range")
    private fun getFileName(contentResolver: ContentResolver, uri: Uri): String? {
        var displayName: String? = null
        val cursor = contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                try {
                    displayName = it.getString(it.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
        }
        return displayName
    }

    /**
     * Handle document selection.
     *
     * @param contentResolver Content resolver
     * @param uri Uri
     */
    private fun handleDocumentSelection(contentResolver: ContentResolver, uri: Uri) {
        try {
            val inputStream = contentResolver.openInputStream(uri)

            val fileReader = inputStream?.readBytes()
            RECIEPT = fileReader!!
            val displayName = getFileName(contentResolver, uri)
            detailReceiptUri.setText(displayName)

            inputStream.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * On activity result.
     *
     * @param requestCode Request code
     * @param resultCode Result code
     * @param data Data
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_FILE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                val contentResolver = this@AddNew.contentResolver
                val mimeType = contentResolver.getType(uri)

                if (mimeType != null) {
                    if (mimeType.startsWith("image/")) {
                        handlePhotoSelection(contentResolver, uri)
                    } else {
                        handleDocumentSelection(contentResolver, uri)
                    }
                }
            }
        }
    }

}