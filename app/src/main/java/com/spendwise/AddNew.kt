package com.spendwise

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.content.ContentResolver
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import java.io.ByteArrayOutputStream
import java.io.File
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
    lateinit var detailTitle: TextInputEditText
    lateinit var detailDate: EditText
    lateinit var detailAmount: TextInputEditText
    lateinit var detailReceiptUri: EditText
    lateinit var detailDesc: TextInputEditText
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
    private var filePath: String = "N/A"
    private var hasReceipt = false
    private var hasFile = false
    private var countryCurrencyMap = HashMap<String, String>(0)
    private lateinit var blobType: String

    /**
     * On create.
     *
     * @param savedInstanceState Saved instance state
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_new)
        val type = intent.type
        val view = layoutInflater.inflate(R.layout.detailsview, null)
        val addNewLinearLayout = findViewById<LinearLayout>(R.id.expenseDetails)
        addNewLinearLayout.addView(view)
        detailTitle = addNewLinearLayout.findViewById(R.id.expenseDetailsTitle)
        detailDate = addNewLinearLayout.findViewById(R.id.expenseDetails_date)
        showCalendarbtn =
            addNewLinearLayout.findViewById(R.id.showCalendarBtn)
        showFilesbtn = addNewLinearLayout.findViewById(R.id.showFilesBtn)

        showCalendarbtn.setOnClickListener {
            showDatePickerDialog()
        }
        showFilesbtn.setOnClickListener {
            getFile()
        }
        detailCategory = addNewLinearLayout.findViewById(R.id.expenseDetails_spinner_category)
        detailMethod = addNewLinearLayout.findViewById(R.id.expenseDetails_spinner_payment_method)

        detailAmount = addNewLinearLayout.findViewById(R.id.expenseDetailsAmount)
        detailReceiptUri = addNewLinearLayout.findViewById(R.id.expenseDetails_reciept)
        detailDesc = addNewLinearLayout.findViewById(R.id.expenseDetailsDesc)
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
        val hrs = calendar.get(Calendar.HOUR_OF_DAY)
        val mins = calendar.get(Calendar.MINUTE)
        val secs = calendar.get(Calendar.SECOND)

        val datePickerDialog = DatePickerDialog(
            this, { _: DatePicker, selectedYear: Int, selectedMonth: Int, selectedDay: Int ->
                val selectedDate =
                    formatDate(selectedYear, selectedMonth, selectedDay, hrs, mins, secs)
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
        val email = getSharedPreferences("credentials", MODE_PRIVATE).getString("email", null)!!

        val database = DatabaseHelper(this@AddNew, email)
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
        val defaultCountry =
            getSharedPreferences("credentials", MODE_PRIVATE).getString("country", "India")
        val sign = Constants.getMap()[defaultCountry]!!

        when (a) {
            1 -> {
                database.insertDataExpense(
                    email,
                    TITLE,
                    DATE,
                    CATEGORY,
                    P_METHOD,
                    AMOUNT, sign,
                    RECIEPT,
                    blobType,
                    DESCRIPTION,
                    hasFile.toString(),
                    filePath
                )

            }

            2 -> {
                database.insertDataExpense(
                    email,
                    TITLE,
                    DATE,
                    CATEGORY,
                    P_METHOD,
                    AMOUNT, sign,
                    blobType,
                    RECIEPT,
                    hasFile.toString(),
                    filePath
                )

            }

            3 -> {
                database.insertDataExpense(
                    email,
                    TITLE,
                    DATE,
                    CATEGORY,
                    P_METHOD,
                    AMOUNT,
                    sign,
                    DESCRIPTION
                )
                Log.e("database", "successfullmine")
            }

            4 -> {
                database.insertDataExpense(email, TITLE, DATE, CATEGORY, P_METHOD, AMOUNT, sign)

            }
        }
        val intent = Intent(this@AddNew, HomeActivity::class.java)
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
            val byteArraySizeInMB = byteArray.size / (1024.0 * 1024.0)
            if (byteArraySizeInMB <= 1.0) {
                RECIEPT = byteArray
                hasFile = false

            } else {
                var file = File(applicationContext.dataDir, displayName!!)
                file.writeBytes(byteArray)
                filePath = file.absolutePath
                RECIEPT = ByteArray(0)
                hasFile = true

            }

            hasReceipt = true
            detailReceiptUri.setText(displayName)
            blobType = "image"

            inputStream?.close()
        } catch (e: Exception) {
            hasReceipt = false
            e.printStackTrace()
            Log.e("error add new", "handleImageSelection: " + e.localizedMessage)
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
            val displayName = getFileName(contentResolver, uri)
            val fileReader = inputStream?.readBytes()!!
            val byteArraySizeInMB = fileReader.size / (1024.0 * 1024.0)
            if (byteArraySizeInMB <= 1.0) {
                RECIEPT = fileReader
                hasFile = false

            } else {
                val file = File(applicationContext.dataDir, displayName!!)
                file.writeBytes(fileReader)
                filePath = file.absolutePath
                RECIEPT = ByteArray(0)
                hasFile = true

            }

            hasReceipt = true
            detailReceiptUri.setText(displayName)
            blobType = "pdf"
            inputStream.close()
        } catch (e: Exception) {
            hasReceipt = false

            Log.e("error add new", "handleDocumentSelection: " + e.localizedMessage)
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
        } else {

            Log.e("onActivityResult", "onActivityResult: canceled")
        }
    }


}