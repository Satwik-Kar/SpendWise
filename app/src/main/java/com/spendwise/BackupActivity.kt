package com.spendwise

import android.app.Activity
import android.content.Intent
import android.icu.util.Calendar
import android.net.Uri
import android.os.Bundle
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
    private val REQUEST_CODE = 122
    private lateinit var BACKUP_TYPE: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_backup)
        lastBackupLocal = this.findViewById(R.id.dateOfLocalBackup)
        lastBackupLocalSize = this.findViewById(R.id.sizeOfBackup)
        backupButton = this.findViewById(R.id.backUpButton)
        val size =
            getSharedPreferences("credentials", MODE_PRIVATE).getString("last_backup_size", null)

        val dateTimeType = getSharedPreferences("credentials", MODE_PRIVATE)
            .getString("last_backup_date", null)
        if (size != null) {
            lastBackupLocalSize.text = size

        } else {
            lastBackupLocalSize.text = "Size: N/A"
        }
        if (dateTimeType != null) {
            lastBackupLocal.text = dateTimeType

        } else {
            lastBackupLocal.text = "No backup found."
        }

        backupButton.setOnClickListener {

            this.initiateBackupInDevice()

        }


    }

    private fun initiateBackupInDevice() {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "application/octet-stream"
        intent.putExtra(Intent.EXTRA_TITLE, Constants.BACKUP_FILE_NAME)

        startActivityForResult(intent, REQUEST_CODE)
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
                    this@BackupActivity,
                    "Backup Successful.",
                    Toast.LENGTH_LONG
                ).show()

            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                performBackup(uri)
            }
        }
    }

    private fun padSize(size: Double): String {


        val decimalPlaces = 2.0
        val roundedVal =
            ((size) * 10.0.pow(decimalPlaces)).roundToInt() / 10.0.pow(
                decimalPlaces
            )

        return "${roundedVal}"


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