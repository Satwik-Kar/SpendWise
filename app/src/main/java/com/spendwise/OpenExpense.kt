package com.spendwise

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.pdf.PdfRenderer
import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.joanzapata.pdfview.PDFView
import com.joanzapata.pdfview.listener.OnDrawListener
import com.joanzapata.pdfview.listener.OnLoadCompleteListener
import java.io.File
import java.io.IOException
import java.lang.IllegalArgumentException
import java.lang.IllegalStateException


/**
 * Open expense
 *
 * @constructor Create empty Open expense
 */
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
    private lateinit var linearLayout: LinearLayout
    private lateinit var secondViewElementImage: View
    private val COLUMN_BLOB_TYPE = "BlobDataType"
    private val COLUMN_BLOB_RECEIPT = "BlobDataReceipt"
    private lateinit var pdfRenderer: PdfRenderer
    private lateinit var currentPage: PdfRenderer.Page
    private var pagerCounter = 0

    /**
     * On create
     *
     * @param savedInstanceState
     */
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
        secondViewElementImage = layoutInflater.inflate(R.layout.image_viewer, null)
        linearLayout = this.findViewById<LinearLayout>(R.id.openExpenseLinearLayout)
        firstViewElement.findViewById<TextView>(R.id.openExpense_title).text = title
        firstViewElement.findViewById<TextView>(R.id.openExpense_date).text = date
        firstViewElement.findViewById<TextView>(R.id.openExpense_category).text = category
        firstViewElement.findViewById<TextView>(R.id.openExpense_p_method).text = pMethod
        firstViewElement.findViewById<TextView>(R.id.openExpense_Desc).text = desc
        firstViewElement.findViewById<TextView>(R.id.openExpense_amount).text = amount
        imageViewViewer = secondViewElementImage.findViewById(R.id.imageViewViewer)
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
        imageViewViewer.setOnClickListener {

            try{
                currentPage.close()
                pagerCounter++
                displayPage(pagerCounter)
            }catch (e:Exception){
                Toast.makeText(this@OpenExpense, "No more page.", Toast.LENGTH_SHORT).show()
            }


        }

    }


    /**
     * Toggle display mode
     *
     * @param type
     */
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

    /**
     * Display pdf
     *
     */
    private fun displayPdf() {

        val byteArray = receipt
        val pdfFile = File(applicationContext.cacheDir, "temporary.pdf")
        pdfFile.writeBytes(byteArray)
        try {
            openPdf(pdfFile)
            displayPage(pagerCounter)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        linearLayout.addView(secondViewElementImage)
        Log.e("ABSOLUTE", "displayPdf: " + pdfFile.absolutePath)

    }

    /**
     * Display image
     *
     */
    private fun displayImage() {
        val bitmap = BitmapFactory.decodeByteArray(receipt, 0, receipt.size)


        imageViewViewer.setImageBitmap(bitmap)
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
    private fun displayPage(index:Int) {


        currentPage = pdfRenderer.openPage(index)

        val bitmap =
            Bitmap.createBitmap(currentPage.width, currentPage.height, Bitmap.Config.ARGB_8888)
        currentPage.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)

        imageViewViewer.setImageBitmap(bitmap)
    }


}