package com.spendwise

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.FileProvider
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import java.io.File
import java.io.IOException


/**
 * Open expense
 *
 * @constructor Create empty Open expense
 */
class OpenExpense : AppCompatActivity() {

    private lateinit var pdfFile: File
    private lateinit var imageFile: File
    lateinit var title: String
    lateinit var id: String
    lateinit var date: String
    lateinit var category: String
    lateinit var pMethod: String
    lateinit var desc: String
    lateinit var amount: String
    lateinit var sign: String
    private lateinit var receipt: ByteArray
    private lateinit var receiptType: String
    private lateinit var imageViewViewer: ImageView
    private lateinit var nextBtn: Button
    private lateinit var prevBtn: Button
    private lateinit var fullScrBtn: FloatingActionButton

    private lateinit var linearLayout: LinearLayout
    private lateinit var secondViewElementImage: View
    private val COLUMN_BLOB_TYPE = "BlobDataType"
    private val COLUMN_BLOB_RECEIPT = "BlobDataReceipt"
    private lateinit var pdfRenderer: PdfRenderer
    private lateinit var currentPage: PdfRenderer.Page
    private var pagerCounter = 0
    private var pages = 0
    private var hasReceipt = false
    private var hasFile = "false"
    private var filePath = ""
    private val COLUMN_HAS_FILE = "HasFile"
    private val COLUMN_FILE_PATH = "FilePath"

    /**
     * On create
     *
     * @param savedInstanceState
     */
    @SuppressLint("Range")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_open_expense)
        val toolbar = this.findViewById<Toolbar>(R.id.openExpenseToolbar)
        toolbar.subtitle = "Expense Details"
        toolbar.setSubtitleTextColor(Color.BLACK)
        toolbar.setTitleTextColor(Color.BLACK)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        toolbar.setNavigationIcon(R.drawable.baseline_arrow_back_ios_24)

        title = intent.getStringExtra("title").toString()
        id = intent.getStringExtra("id").toString()
        date = intent.getStringExtra("date").toString()
        category = intent.getStringExtra("category").toString()
        pMethod = intent.getStringExtra("p_method").toString()
        desc = intent.getStringExtra("desc").toString()
        amount = intent.getStringExtra("amount").toString()
        sign = intent.getStringExtra("sign").toString()
        val firstViewElement = layoutInflater.inflate(R.layout.open_expense_first_element, null)
        secondViewElementImage = layoutInflater.inflate(R.layout.image_viewer, null)
        linearLayout = this.findViewById(R.id.openExpenseLinearLayout)
        firstViewElement.findViewById<TextView>(R.id.openExpense_title).text = title
        firstViewElement.findViewById<TextView>(R.id.openExpense_date).text = date
        firstViewElement.findViewById<TextView>(R.id.openExpense_category).text = category
        firstViewElement.findViewById<TextView>(R.id.openExpense_p_method).text = pMethod
        firstViewElement.findViewById<TextView>(R.id.openExpense_Desc).text = desc
        firstViewElement.findViewById<TextView>(R.id.openExpense_amount).text = "$sign $amount"
        imageViewViewer = secondViewElementImage.findViewById(R.id.imageViewViewer)
        nextBtn = secondViewElementImage.findViewById(R.id.nextBtn)
        prevBtn = secondViewElementImage.findViewById(R.id.previousBtn)
        fullScrBtn = secondViewElementImage.findViewById(R.id.fullScreen)
        linearLayout.addView(firstViewElement)

        val email = getSharedPreferences("credentials", MODE_PRIVATE).getString("email", null)!!
        val database = DatabaseHelper(applicationContext, email)
        val cursor = database.retrieveExpenseDataById(id)
        cursor.use {
            while (cursor != null && cursor.moveToNext()) {
                try {
                    hasFile = it!!.getString(it.getColumnIndex(COLUMN_HAS_FILE))
                    if (hasFile == true.toString()) {

                        filePath = it.getString(it.getColumnIndex(COLUMN_FILE_PATH))

                    } else {
                        receipt = it.getBlob(it.getColumnIndex(COLUMN_BLOB_RECEIPT))
                    }

                    receiptType = it.getString(it.getColumnIndex(COLUMN_BLOB_TYPE))
                    hasReceipt = true
                } catch (e: NullPointerException) {
                    hasReceipt = false
                    receiptType = "N/A"
                }
            }
        }

        toggleDisplayMode(receiptType, hasReceipt)
        prevBtn.setOnClickListener {

            try {

                pagerCounter--
                displayPage(pagerCounter)

            } catch (e: Exception) {
                Log.e("pageerror", "onCreate: $e")
                Toast.makeText(this@OpenExpense, "No more previous page.", Toast.LENGTH_SHORT)
                    .show()
                pagerCounter = 0
            }


        }
        nextBtn.setOnClickListener {
            try {
                pagerCounter++
                displayPage(pagerCounter)
            } catch (e: Exception) {
                Toast.makeText(this@OpenExpense, "No more page.", Toast.LENGTH_SHORT).show()
                pagerCounter = pages
                Log.e("pageerror", "onCreate: $e")
            }
        }
        fullScrBtn.setOnClickListener {
            if (receiptType == "pdf") {
                val uri: Uri =
                    FileProvider.getUriForFile(this, "com.spendwise.fileprovider", pdfFile)
                val intent = Intent(Intent.ACTION_VIEW)
                intent.setDataAndType(uri, "application/pdf")
                intent.flags =
                    Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_ACTIVITY_NO_ANIMATION

                try {
                    startActivity(intent)
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            } else if (receiptType == "image") {
                val imageUri = FileProvider.getUriForFile(
                    this, "${packageName}.fileprovider", imageFile
                )

                val intent = Intent(Intent.ACTION_VIEW)
                intent.setDataAndType(imageUri, "image/*")
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

                try {
                    startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    // Handle the case where no image viewer app is installed
                    Toast.makeText(this, "No image viewer app found", Toast.LENGTH_SHORT).show()
                }

            }


        }


    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.expense_details_menu_items, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val idItem = item.itemId
        if (idItem == R.id.action_delete) {
            val alert = AlertDialog.Builder(this@OpenExpense)
            alert.setPositiveButton("Delete") { _, _ ->
                val email =
                    getSharedPreferences("credentials", MODE_PRIVATE).getString("email", null)!!
                val database = DatabaseHelper(this, email)
                try {

                    database.deleteExpenseDataById(id)
                    if (hasFile == true.toString()) {
                        try {
                            pdfFile.delete()
                        } catch (e: Exception) {
                            Log.e("delete", "onCreate: ${e.message}")
                        }
                        try {
                            imageFile.delete()
                        } catch (e: Exception) {
                            Log.e("delete", "onCreate: ${e.message}")

                        }

                    }
                    val intent = Intent(this@OpenExpense, HomeActivity::class.java)
                    startActivity(intent)
                    finishAffinity()
                    Toast.makeText(this@OpenExpense, "Deleted", Toast.LENGTH_LONG).show()

                } catch (e: Exception) {
                    Log.e("delete", "onCreate: cannot delete data" + e.message)
                }


            }
            alert.setNegativeButton("No") { _, _ ->

                Toast.makeText(this@OpenExpense, "Delete Cancelled", Toast.LENGTH_LONG).show()
            }
            alert.setMessage("Are you sure to delete this expense?")
            alert.setTitle("Delete?")
            alert.show()
            return true
        } else if (idItem == R.id.action_edit) {
            val alert = AlertDialog.Builder(this@OpenExpense)
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
                    val database = DatabaseHelper(this@OpenExpense, email)
                    database.updateData(
                        id,
                        titleInner.text.toString(),
                        amountInner.text.toString(),
                        descInner.text.toString()
                    )
                    startActivity(Intent(this@OpenExpense, HomeActivity::class.java))
                    finishAffinity()
                    Toast.makeText(this@OpenExpense, "Edited Successfully", Toast.LENGTH_SHORT)
                        .show()
                } else {

                    Toast.makeText(
                        this@OpenExpense, "Fill up the required fields.", Toast.LENGTH_SHORT
                    ).show()

                }


            }
            alert.setNegativeButton("Cancel") { _, _ ->
                Toast.makeText(this@OpenExpense, "Cancelled", Toast.LENGTH_SHORT).show()

            }
            alert.show()

            return true
        }
        return super.onOptionsItemSelected(item)
    }


    /**
     * Toggle display mode
     *
     * @param type
     * @param hasReceipt
     */
    private fun toggleDisplayMode(type: String, hasReceipt: Boolean) {
        if (!hasReceipt) {
            return
        }
        when (type) {
            "image" -> {
                displayImage()
                nextBtn.visibility = View.GONE
                prevBtn.visibility = View.GONE
            }

            "pdf" -> {

                displayPdf()
                nextBtn.visibility = View.VISIBLE
                prevBtn.visibility = View.VISIBLE
            }
        }
    }

    /**
     * Display pdf
     *
     */
    private fun displayPdf() {
        lateinit var byteArray: ByteArray
        Log.e("hasFile", "displayImage: $hasFile")

        if (hasFile == true.toString()) {
            val filePath = filePath
            pdfFile = File(filePath)
            if (pdfFile.exists()) {
                try {
                    openPdf(pdfFile)
                    displayPage(pagerCounter)
                    linearLayout.addView(secondViewElementImage)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            } else {
                Toast.makeText(this@OpenExpense, "File does not exist.", Toast.LENGTH_SHORT).show()

            }


        } else {
            pdfFile = File(applicationContext.cacheDir, "temp.pdf")
            pdfFile.writeBytes(receipt)
            if (pdfFile.exists()) {
                try {
                    openPdf(pdfFile)
                    displayPage(pagerCounter)
                    linearLayout.addView(secondViewElementImage)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            } else {
                Toast.makeText(this@OpenExpense, "File does not exist.", Toast.LENGTH_SHORT).show()

            }
        }


    }

    /**
     * Display image
     *
     */
    private fun displayImage() {
        Log.e("hasFile", "displayImage: $hasFile")
        if (hasFile == true.toString()) {
            val options = BitmapFactory.Options()
            options.inPreferredConfig = Bitmap.Config.ARGB_8888
            val bitmap = BitmapFactory.decodeFile(filePath, options)
            imageFile = File(filePath)
            imageViewViewer.setImageBitmap(bitmap)


        } else {
            val bitmap = BitmapFactory.decodeByteArray(receipt, 0, receipt.size)
            imageViewViewer.setImageBitmap(bitmap)
            linearLayout.addView(secondViewElementImage)
            imageFile = File(applicationContext.cacheDir, "temporary.jpg")
            imageFile.writeBytes(receipt)


        }
        linearLayout.removeView(secondViewElementImage)

        linearLayout.addView(secondViewElementImage)
    }

    /**
     * Open pdf
     *
     * @param file
     */
    private fun openPdf(file: File) {
        val fileDescriptor: ParcelFileDescriptor =
            ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
        pdfRenderer = PdfRenderer(fileDescriptor)
    }

    /**
     * Display page
     *
     */
    private fun displayPage(index: Int) {

        try {
            currentPage.close()
        } catch (e: Exception) {
            Log.e("pageerror", "displayPage: $e")
        }

        currentPage = pdfRenderer.openPage(index)

        val bitmap =
            Bitmap.createBitmap(currentPage.width, currentPage.height, Bitmap.Config.ARGB_8888)
        currentPage.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
        pages = pdfRenderer.pageCount
        imageViewViewer.setImageBitmap(bitmap)
    }


}