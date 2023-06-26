package com.spendwise

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import android.widget.Toast

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    val CONTEXT = context
    companion object {
        private const val DATABASE_NAME = "ExpenseDetails.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_NAME = "Expenses"
        private const val COLUMN_TITLE = "title"
        private const val COLUMN_DATE = "date"
        private const val COLUMN_CATEGORY = "category"
        private const val COLUMN_P_METHOD = "p_method"
        private const val COLUMN_AMOUNT = "amount"
        private const val COLUMN_BLOB_RECEIPT = "BlobDataReceipt"
        private const val COLUMN_DESCRIPTION = "description"
    }

    override fun onCreate(db: SQLiteDatabase) {
        // Create your database table
        val createTableQuery = "CREATE TABLE $TABLE_NAME " +
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COLUMN_TITLE TEXT, " +
                "$COLUMN_DATE TEXT, " +
                "$COLUMN_CATEGORY TEXT, " +
                "$COLUMN_P_METHOD TEXT, " +
                "$COLUMN_AMOUNT TEXT, " +
                "$COLUMN_BLOB_RECEIPT BLOB, " +
                "$COLUMN_DESCRIPTION TEXT)"
        db.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Perform necessary actions when upgrading the database
        // For simplicity, we'll drop the table and recreate it
        val dropTableQuery = "DROP TABLE IF EXISTS $TABLE_NAME"
        db.execSQL(dropTableQuery)
        onCreate(db)
    }

    fun insertData(
        title: String,
        date: String,
        category: String,
        p_method: String,
        amount: String,
        blobFileData: ByteArray,
        description: String
    ) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_TITLE, title)
            put(COLUMN_DATE, date)
            put(COLUMN_CATEGORY, category)
            put(COLUMN_P_METHOD, p_method)
            put(COLUMN_AMOUNT, amount)
            put(COLUMN_BLOB_RECEIPT, blobFileData)
            put(COLUMN_DESCRIPTION, description)
        }
        db.insert(TABLE_NAME, null, values)
        db.close()
    }

    fun insertData(
        title: String,
        date: String,
        category: String,
        p_method: String,
        amount: String,
    ) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_TITLE, title)
            put(COLUMN_DATE, date)
            put(COLUMN_CATEGORY, category)
            put(COLUMN_P_METHOD, p_method)
            put(COLUMN_AMOUNT, amount)
        }
        db.insert(TABLE_NAME, null, values)
        db.close()
    }

    fun insertData(
        title: String,
        date: String,
        category: String,
        p_method: String,
        amount: String,
        blobFileData: ByteArray
    ) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_TITLE, title)
            put(COLUMN_DATE, date)
            put(COLUMN_CATEGORY, category)
            put(COLUMN_P_METHOD, p_method)
            put(COLUMN_AMOUNT, amount)
            put(COLUMN_BLOB_RECEIPT, blobFileData)
        }
        db.insert(TABLE_NAME, null, values)
        db.close()
    }

    fun insertData(
        title: String,
        date: String,
        category: String,
        p_method: String,
        amount: String,
        description: String
    ) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_TITLE, title)
            put(COLUMN_DATE, date)
            put(COLUMN_CATEGORY, category)
            put(COLUMN_P_METHOD, p_method)
            put(COLUMN_AMOUNT, amount)
            put(COLUMN_DESCRIPTION, description)
        }
        db.insert(TABLE_NAME, null, values)
        db.close()
        Log.e("database", "successfull")
    }


    fun retrieveData(): Cursor? {
        val db = readableDatabase
        return db.query(TABLE_NAME, null, null, null, null, null, null)
    }
}
