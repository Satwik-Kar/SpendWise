package com.spendwise

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.joanzapata.pdfview.PDFView
import com.joanzapata.pdfview.listener.OnLoadCompleteListener
import java.io.File
import java.nio.charset.StandardCharsets

class OpenExpense : AppCompatActivity() {

    lateinit var title: String
    lateinit var id: String
    lateinit var date: String
    lateinit var category: String
    lateinit var pMethod: String
    lateinit var desc: String
    lateinit var amount: String
    private lateinit var receipt: ByteArray
    private lateinit var receiptType: String
    private lateinit var pdfView: PDFView
    private lateinit var imageViewViewer: ImageView
    private val COLUMN_BLOB_TYPE = "BlobDataType"
    private val COLUMN_BLOB_RECEIPT = "BlobDataReceipt"

    @SuppressLint("Range")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_open_expense)
        title = intent.getStringExtra("title").toString()
        id = intent.getStringExtra("id").toString()
        date = intent.getStringExtra("date").toString()
        category = intent.getStringExtra("category").toString()
        pMethod = intent.getStringExtra("p_method").toString()
        desc = intent.getStringExtra("desc").toString()
        amount = intent.getStringExtra("amount").toString()
        val firstViewElement = layoutInflater.inflate(R.layout.open_expense_first_element, null)
        val secondViewElement = layoutInflater.inflate(R.layout.pdf_view, null)
        val linearLayout = this.findViewById<LinearLayout>(R.id.openExpenseLinearLayout)
        firstViewElement.findViewById<TextView>(R.id.openExpense_title).text = title
        firstViewElement.findViewById<TextView>(R.id.openExpense_date).text = date
        firstViewElement.findViewById<TextView>(R.id.openExpense_category).text = category
        firstViewElement.findViewById<TextView>(R.id.openExpense_p_method).text = pMethod
        firstViewElement.findViewById<TextView>(R.id.openExpense_Desc).text = desc
        firstViewElement.findViewById<TextView>(R.id.openExpense_amount).text = amount
        pdfView = secondViewElement.findViewById(R.id.pdfView)
        imageViewViewer = secondViewElement.findViewById(R.id.imageViewViewer)
        linearLayout.addView(firstViewElement)

        val database = DatabaseHelper(applicationContext)
        val cursor = database.retrieveDataById(id)
        cursor.use {
            while (cursor != null && cursor.moveToNext()) {
                receipt = it!!.getBlob(it.getColumnIndex(COLUMN_BLOB_RECEIPT))
                receiptType = it.getString(it.getColumnIndex(COLUMN_BLOB_TYPE))

            }
        }

        toggleDisplayMode(receiptType)

        linearLayout.addView(secondViewElement)
    }


    private fun toggleDisplayMode(type: String) {
        when (type) {
            "image" -> {
                displayImage()
            }

            "pdf" -> {
                displayPdf()
            }
        }
    }

    private fun displayPdf() {
        imageViewViewer.visibility = View.INVISIBLE
        pdfView.visibility = View.VISIBLE

        val byteArray = receipt
        val pdfFile = File.createTempFile("temp", ".pdf",applicationContext.cacheDir)
        pdfFile.writeBytes(byteArray)
        pdfView.fromFile(pdfFile)
            .onLoad(OnLoadCompleteListener {
                Log.e("PDFVIEWER", "displayPdf: completed LOAD-$it")


            })
            .load()
        Log.e("ABSOLUTE", "displayPdf: "+pdfFile.absolutePath)
    }

    private fun displayImage() {
        pdfView.visibility = View.INVISIBLE
        imageViewViewer.visibility = View.VISIBLE
        val bitmap = BitmapFactory.decodeByteArray(receipt, 0, receipt.size)


        imageViewViewer.setImageBitmap(bitmap)
    }


}