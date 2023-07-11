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
        countryCurrencyMap["Afghanistan"] = "؋"
        countryCurrencyMap["Albania"] = "L"
        countryCurrencyMap["Algeria"] = "د.ج"
        countryCurrencyMap["Andorra"] = "€"
        countryCurrencyMap["Angola"] = "Kz"
        countryCurrencyMap["Antigua and Barbuda"] = "$"
        countryCurrencyMap["Argentina"] = "$"
        countryCurrencyMap["Armenia"] = "֏"
        countryCurrencyMap["Australia"] = "$"
        countryCurrencyMap["Austria"] = "€"
        countryCurrencyMap["Azerbaijan"] = "₼"
        countryCurrencyMap["Bahamas"] = "$"
        countryCurrencyMap["Bahrain"] = ".د.ب"
        countryCurrencyMap["Bangladesh"] = "৳"
        countryCurrencyMap["Barbados"] = "$"
        countryCurrencyMap["Belarus"] = "Br"
        countryCurrencyMap["Belgium"] = "€"
        countryCurrencyMap["Belize"] = "$"
        countryCurrencyMap["Benin"] = "CFA"
        countryCurrencyMap["Bhutan"] = "Nu."
        countryCurrencyMap["Bolivia"] = "Bs."
        countryCurrencyMap["Bosnia and Herzegovina"] = "KM"
        countryCurrencyMap["Botswana"] = "P"
        countryCurrencyMap["Brazil"] = "R$"
        countryCurrencyMap["Brunei"] = "$"
        countryCurrencyMap["Bulgaria"] = "лв"
        countryCurrencyMap["Burkina Faso"] = "CFA"
        countryCurrencyMap["Burundi"] = "Fr"
        countryCurrencyMap["Cambodia"] = "៛"
        countryCurrencyMap["Cameroon"] = "CFA"
        countryCurrencyMap["Canada"] = "$"
        countryCurrencyMap["Cape Verde"] = "CVE"
        countryCurrencyMap["Central African Republic"] = "CFA"
        countryCurrencyMap["Chad"] = "CFA"
        countryCurrencyMap["Chile"] = "$"
        countryCurrencyMap["China"] = "¥"
        countryCurrencyMap["Colombia"] = "$"
        countryCurrencyMap["Comoros"] = "Fr"
        countryCurrencyMap["Congo, Democratic Republic of the"] = "CDF"
        countryCurrencyMap["Congo, Republic of the"] = "CFA"
        countryCurrencyMap["Costa Rica"] = "₡"
        countryCurrencyMap["Croatia"] = "kn"
        countryCurrencyMap["Cuba"] = "$"
        countryCurrencyMap["Cyprus"] = "€"
        countryCurrencyMap["Czech Republic"] = "Kč"
        countryCurrencyMap["Denmark"] = "kr"
        countryCurrencyMap["Djibouti"] = "Fr"
        countryCurrencyMap["Dominica"] = "$"
        countryCurrencyMap["Dominican Republic"] = "RD$"
        countryCurrencyMap["East Timor"] = "USD"
        countryCurrencyMap["Ecuador"] = "USD"
        countryCurrencyMap["Egypt"] = "ج.م"
        countryCurrencyMap["El Salvador"] = "$"
        countryCurrencyMap["Equatorial Guinea"] = "CFA"
        countryCurrencyMap["Eritrea"] = "Nfk"
        countryCurrencyMap["Estonia"] = "€"
        countryCurrencyMap["Eswatini"] = "SZL"
        countryCurrencyMap["Ethiopia"] = "ETB"
        countryCurrencyMap["Fiji"] = "$"
        countryCurrencyMap["Finland"] = "€"
        countryCurrencyMap["France"] = "€"
        countryCurrencyMap["Gabon"] = "CFA"
        countryCurrencyMap["Gambia"] = "GMD"
        countryCurrencyMap["Georgia"] = "₾"
        countryCurrencyMap["Germany"] = "€"
        countryCurrencyMap["Ghana"] = "₵"
        countryCurrencyMap["Greece"] = "€"
        countryCurrencyMap["Grenada"] = "$"
        countryCurrencyMap["Guatemala"] = "Q"
        countryCurrencyMap["Guinea"] = "GNF"
        countryCurrencyMap["Guinea-Bissau"] = "CFA"
        countryCurrencyMap["Guyana"] = "$"
        countryCurrencyMap["Haiti"] = "G"
        countryCurrencyMap["Honduras"] = "L"
        countryCurrencyMap["Hungary"] = "Ft"
        countryCurrencyMap["Iceland"] = "kr"
        countryCurrencyMap["India"] = "₹"
        countryCurrencyMap["Indonesia"] = "Rp"
        countryCurrencyMap["Iran"] = "﷼"
        countryCurrencyMap["Iraq"] = "ع.د"
        countryCurrencyMap["Ireland"] = "€"
        countryCurrencyMap["Israel"] = "₪"
        countryCurrencyMap["Italy"] = "€"
        countryCurrencyMap["Jamaica"] = "J$"
        countryCurrencyMap["Japan"] = "¥"
        countryCurrencyMap["Jordan"] = "د.ا"
        countryCurrencyMap["Kazakhstan"] = "₸"
        countryCurrencyMap["Kenya"] = "KSh"
        countryCurrencyMap["Kiribati"] = "$"
        countryCurrencyMap["Korea, North"] = "₩"
        countryCurrencyMap["Korea, South"] = "₩"
        countryCurrencyMap["Kosovo"] = "€"
        countryCurrencyMap["Kuwait"] = "د.ك"
        countryCurrencyMap["Kyrgyzstan"] = "лв"
        countryCurrencyMap["Laos"] = "₭"
        countryCurrencyMap["Latvia"] = "€"
        countryCurrencyMap["Lebanon"] = "ل.ل"
        countryCurrencyMap["Lesotho"] = "L"
        countryCurrencyMap["Liberia"] = "L$"
        countryCurrencyMap["Libya"] = "ل.د"
        countryCurrencyMap["Liechtenstein"] = "CHF"
        countryCurrencyMap["Lithuania"] = "€"
        countryCurrencyMap["Luxembourg"] = "€"
        countryCurrencyMap["Madagascar"] = "Ar"
        countryCurrencyMap["Malawi"] = "MK"
        countryCurrencyMap["Malaysia"] = "RM"
        countryCurrencyMap["Maldives"] = "ރ."
        countryCurrencyMap["Mali"] = "CFA"
        countryCurrencyMap["Malta"] = "€"
        countryCurrencyMap["Marshall Islands"] = "$"
        countryCurrencyMap["Mauritania"] = "MRU"
        countryCurrencyMap["Mauritius"] = "₨"
        countryCurrencyMap["Mexico"] = "$"
        countryCurrencyMap["Micronesia"] = "$"
        countryCurrencyMap["Moldova"] = "MDL"
        countryCurrencyMap["Monaco"] = "€"
        countryCurrencyMap["Mongolia"] = "₮"
        countryCurrencyMap["Montenegro"] = "€"
        countryCurrencyMap["Morocco"] = "د.م."
        countryCurrencyMap["Mozambique"] = "MT"
        countryCurrencyMap["Myanmar"] = "Ks"
        countryCurrencyMap["Namibia"] = "$"
        countryCurrencyMap["Nauru"] = "$"
        countryCurrencyMap["Nepal"] = "₨"
        countryCurrencyMap["Netherlands"] = "€"
        countryCurrencyMap["New Zealand"] = "$"
        countryCurrencyMap["Nicaragua"] = "C$"
        countryCurrencyMap["Niger"] = "CFA"
        countryCurrencyMap["Nigeria"] = "₦"
        countryCurrencyMap["North Macedonia"] = "ден"
        countryCurrencyMap["Norway"] = "kr"
        countryCurrencyMap["Oman"] = "ر.ع."
        countryCurrencyMap["Pakistan"] = "₨"
        countryCurrencyMap["Palau"] = "$"
        countryCurrencyMap["Panama"] = "B/."
        countryCurrencyMap["Papua New Guinea"] = "K"
        countryCurrencyMap["Paraguay"] = "₲"
        countryCurrencyMap["Peru"] = "S/."
        countryCurrencyMap["Philippines"] = "₱"
        countryCurrencyMap["Poland"] = "zł"
        countryCurrencyMap["Portugal"] = "€"
        countryCurrencyMap["Qatar"] = "ر.ق"
        countryCurrencyMap["Romania"] = "lei"
        countryCurrencyMap["Russia"] = "₽"
        countryCurrencyMap["Rwanda"] = "Fr"
        countryCurrencyMap["Saint Kitts and Nevis"] = "$"
        countryCurrencyMap["Saint Lucia"] = "$"
        countryCurrencyMap["Saint Vincent and the Grenadines"] = "$"
        countryCurrencyMap["Samoa"] = "T"
        countryCurrencyMap["San Marino"] = "€"
        countryCurrencyMap["Sao Tome and Principe"] = "Db"
        countryCurrencyMap["Saudi Arabia"] = "﷼"
        countryCurrencyMap["Senegal"] = "CFA"
        countryCurrencyMap["Serbia"] = "дин."
        countryCurrencyMap["Seychelles"] = "₨"
        countryCurrencyMap["Sierra Leone"] = "Le"
        countryCurrencyMap["Singapore"] = "$"
        countryCurrencyMap["Slovakia"] = "€"
        countryCurrencyMap["Slovenia"] = "€"
        countryCurrencyMap["Solomon Islands"] = "$"
        countryCurrencyMap["Somalia"] = "S"
        countryCurrencyMap["South Africa"] = "R"
        countryCurrencyMap["South Sudan"] = "£"
        countryCurrencyMap["Spain"] = "€"
        countryCurrencyMap["Sri Lanka"] = "Rs"
        countryCurrencyMap["Sudan"] = "SDG"
        countryCurrencyMap["Suriname"] = "$"
        countryCurrencyMap["Sweden"] = "kr"
        countryCurrencyMap["Switzerland"] = "Fr"
        countryCurrencyMap["Syria"] = "£"
        countryCurrencyMap["Taiwan"] = "NT$"
        countryCurrencyMap["Tajikistan"] = "ЅМ"
        countryCurrencyMap["Tanzania"] = "Sh"
        countryCurrencyMap["Thailand"] = "฿"
        countryCurrencyMap["Togo"] = "CFA"
        countryCurrencyMap["Tonga"] = "T$"
        countryCurrencyMap["Trinidad and Tobago"] = "TT$"
        countryCurrencyMap["Tunisia"] = "د.ت"
        countryCurrencyMap["Turkey"] = "₺"
        countryCurrencyMap["Turkmenistan"] = "T"
        countryCurrencyMap["Tuvalu"] = "$"
        countryCurrencyMap["Uganda"] = "Sh"
        countryCurrencyMap["Ukraine"] = "₴"
        countryCurrencyMap["United Arab Emirates"] = "AED"
        countryCurrencyMap["United Kingdom"] = "£"
        countryCurrencyMap["United States"] = "$"
        countryCurrencyMap["Uruguay"] = "$"
        countryCurrencyMap["Uzbekistan"] = "so'm"
        countryCurrencyMap["Vanuatu"] = "Vt"
        countryCurrencyMap["Vatican City"] = "€"
        countryCurrencyMap["Venezuela"] = "Bs."
        countryCurrencyMap["Vietnam"] = "₫"
        countryCurrencyMap["Yemen"] = "﷼"
        countryCurrencyMap["Zambia"] = "ZK"
        countryCurrencyMap["Zimbabwe"] = "Z$"


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
        val email = getSharedPreferences("credentials", MODE_PRIVATE).getString("email", null)!!
        val tableName = removeDotsAndNumbers(email)
        val database = DatabaseHelper(this@AddNew, tableName)
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
        val sign = countryCurrencyMap[defaultCountry]

        when (a) {
            1 -> {
                database.insertData(
                    TITLE,
                    DATE,
                    CATEGORY,
                    P_METHOD,
                    "$sign $AMOUNT",
                    RECIEPT,
                    blobType,
                    DESCRIPTION,
                    hasFile.toString(),
                    filePath
                )

            }

            2 -> {
                database.insertData(
                    TITLE,
                    DATE,
                    CATEGORY,
                    P_METHOD,
                    "$sign $AMOUNT",
                    blobType,
                    RECIEPT,
                    hasFile.toString(),
                    filePath
                )

            }

            3 -> {
                database.insertData(TITLE, DATE, CATEGORY, P_METHOD, "$sign $AMOUNT", DESCRIPTION)
                Log.e("database", "successfullmine")
            }

            4 -> {
                database.insertData(TITLE, DATE, CATEGORY, P_METHOD, "$sign $AMOUNT")

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

    fun removeDotsAndNumbers(email: String): String {
        val pattern = Regex("[.0-9@]")
        return pattern.replace(email, "")
    }

}