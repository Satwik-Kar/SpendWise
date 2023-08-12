package com.spendwise

import android.app.Activity
import android.content.Intent
import android.icu.util.Calendar
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import java.io.FileInputStream
import kotlin.math.pow
import kotlin.math.roundToInt


class BackupActivity : AppCompatActivity() {
    private lateinit var lastBackupLocal: TextView
    private lateinit var lastBackupLocalSize: TextView
    private lateinit var backupButton: Button
    private lateinit var restoreButton: Button
    private val REQUEST_CODE_BACKUP = 122
    private val REQUEST_CODE_RESTORE = 123
    private lateinit var BACKUP_TYPE: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_backup)
        lastBackupLocal = this.findViewById(R.id.dateOfLocalBackup)
        lastBackupLocalSize = this.findViewById(R.id.sizeOfBackup)
        backupButton = this.findViewById(R.id.backUpButton)
        restoreButton = this.findViewById(R.id.restoreButton)
        val size =
            getSharedPreferences("credentials", MODE_PRIVATE).getString("last_backup_size", null)

        val dateTimeType =
            getSharedPreferences("credentials", MODE_PRIVATE).getString("last_backup_date", null)
        if (size != null) {
            lastBackupLocalSize.text = size

        } else {
            lastBackupLocalSize.visibility = View.INVISIBLE
        }
        if (dateTimeType != null) {
            lastBackupLocal.text = dateTimeType

        } else {
            lastBackupLocal.text = "No backup found."
        }

        backupButton.setOnClickListener {

            this.initiateBackupInDevice()

        }
        restoreButton.setOnClickListener {

            this.initiateRestoreInDevice()
        }


    }

    private fun initiateBackupInDevice() {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "application/octet-stream"
        intent.putExtra(
            Intent.EXTRA_TITLE,
            "${
                getDateTime().replace(Regex("[/:\\s]"), "")
            }_${Constants.BACKUP_FILE_NAME}"
        )
        startActivityForResult(intent, REQUEST_CODE_BACKUP)
    }

    private fun initiateRestoreInDevice() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "*/*"
        startActivityForResult(intent, REQUEST_CODE_RESTORE)
    }

    private fun performBackup(uri: Uri) {
        val sourceDBPath = this@BackupActivity.getDatabasePath(Constants.DATABASE_NAME).absolutePath

        val sourceDBFile = File(sourceDBPath)
        val outputStream = this@BackupActivity.contentResolver.openOutputStream(uri)

        outputStream?.use { output ->
            FileInputStream(sourceDBFile).use { input ->
                val bufferSize = 1024
                val buffer = ByteArray(bufferSize)
                var bytesRead: Int
                var totalBytesCopied = 0L

                while (input.read(buffer).also { bytesRead = it } > 0) {
                    output.write(buffer, 0, bytesRead)
                    totalBytesCopied += bytesRead
                }

                val copiedDataSizeMB = totalBytesCopied.toDouble() / (1024 * 1024)
                val padded = padSize(copiedDataSizeMB)
                val text = "Size: $padded MB"
                lastBackupLocalSize.text = text
                getSharedPreferences("credentials", MODE_PRIVATE).edit()
                    .putString("last_backup_size", text).apply()
                BACKUP_TYPE = "local"
                val dateTimeType = "Local: ${getDateTime()}"
                getSharedPreferences("credentials", MODE_PRIVATE).edit()
                    .putString("last_backup_date", dateTimeType).apply()
                lastBackupLocal.text = dateTimeType

                Toast.makeText(
                    this@BackupActivity, "Backup Successful.", Toast.LENGTH_LONG
                ).show()

            }
        }

    }

    private fun performRestore(uri: Uri) {
        val sourceDBPath = applicationContext.getDatabasePath(Constants.DATABASE_NAME).absolutePath
        val sourceDBFile = File(sourceDBPath)
        if (isValidSQLiteFile(uri)) {
            try {
                val contentResolver = applicationContext.contentResolver
                val inputStream = contentResolver.openInputStream(uri)

                if (inputStream != null) {
                    sourceDBFile.outputStream().use { output ->
                        inputStream.copyTo(output, bufferSize = 1024)
                    }

                    Toast.makeText(
                        this@BackupActivity, "Database restored successfully!", Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        this@BackupActivity,
                        "Unable to open input stream for the selected file",
                        Toast.LENGTH_LONG
                    ).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(
                    this@BackupActivity,
                    "Error restoring the database: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        } else {
            Toast.makeText(
                this@BackupActivity,
                "This is not a valid backup file for SpendWise",
                Toast.LENGTH_LONG
            ).show()


        }


    }


    private fun isValidSQLiteFile(fileUri: Uri): Boolean {


        val fileExtension = MimeTypeMap.getFileExtensionFromUrl(fileUri.toString())
        if (fileExtension != null && fileExtension.equals("bak", ignoreCase = true)) {

            val mimeType = contentResolver.getType(fileUri)
            if (mimeType != null && mimeType == "application/octet-stream") {

                return true
            }
        }
        return false


    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_BACKUP && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                performBackup(uri)
            }
        }
        if (requestCode == REQUEST_CODE_RESTORE && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                performRestore(uri)
            }
        }
    }

    private fun padSize(size: Double): String {


        val decimalPlaces = 2.0
        val roundedVal = ((size) * 10.0.pow(decimalPlaces)).roundToInt() / 10.0.pow(
            decimalPlaces
        )

        return "${roundedVal}"


    }

    private fun formatDate(
        year: Int, month: Int, day: Int, hrs: Int, mins: Int, secs: Int
    ): String {
        val formattedMonth = if (month + 1 < 10) "0${month + 1}" else "${month + 1}"
        val formattedDay = if (day < 10) "0$day" else "$day"
        return "$formattedDay/$formattedMonth/$year  $hrs:$mins:$secs"
    }

    private fun getDateTime(): String {

        val cal = Calendar.getInstance()

        val hours = cal.get(Calendar.HOUR_OF_DAY)
        val mins = cal.get(Calendar.MINUTE)
        val secs = cal.get(Calendar.SECOND)

        val day = cal.get(Calendar.DAY_OF_MONTH)
        val month = cal.get(Calendar.MONTH)
        val year = cal.get(Calendar.YEAR)

        return formatDate(year, month, day, hours, mins, secs)


    }
}