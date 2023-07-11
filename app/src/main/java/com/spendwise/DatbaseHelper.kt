package com.spendwise

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class DatabaseHelper(context: Context, userID: String) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {


    private val TABLE_NAME = userID

    companion object {
        private const val DATABASE_NAME = "ExpenseDetails.db"
        private const val DATABASE_VERSION = 1
        private const val COLUMN_TITLE = "title"
        private const val COLUMN_DATE = "date"
        private const val COLUMN_CATEGORY = "category"
        private const val COLUMN_P_METHOD = "p_method"
        private const val COLUMN_AMOUNT = "amount"
        private const val COLUMN_BLOB_RECEIPT = "BlobDataReceipt"
        private const val COLUMN_BLOB_TYPE = "BlobDataType"
        private const val COLUMN_HAS_FILE = "HasFile"
        private const val COLUMN_FILE_PATH = "FilePath"
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
                "$COLUMN_BLOB_TYPE TEXT," +
                "$COLUMN_HAS_FILE TEXT," +
                "$COLUMN_FILE_PATH TEXT," +
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
        blobType: String,
        description: String,
        hasFile: String,
        filePath: String
    ) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_TITLE, title)
            put(COLUMN_DATE, date)
            put(COLUMN_CATEGORY, category)
            put(COLUMN_P_METHOD, p_method)
            put(COLUMN_AMOUNT, amount)
            put(COLUMN_BLOB_RECEIPT, blobFileData)
            put(COLUMN_BLOB_TYPE, blobType)
            put(COLUMN_DESCRIPTION, description)
            put(COLUMN_HAS_FILE, hasFile)
            put(COLUMN_FILE_PATH, filePath)

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
        blobType: String,
        blobFileData: ByteArray,
        hasFile: String,
        filePath: String
    ) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_TITLE, title)
            put(COLUMN_DATE, date)
            put(COLUMN_CATEGORY, category)
            put(COLUMN_P_METHOD, p_method)
            put(COLUMN_AMOUNT, amount)
            put(COLUMN_BLOB_RECEIPT, blobFileData)
            put(COLUMN_BLOB_TYPE, blobType)
            put(COLUMN_HAS_FILE, hasFile)
            put(COLUMN_FILE_PATH, filePath)
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

    fun retrieveDataById(id: String): Cursor? {
        val db = readableDatabase
        val selection = "_id = ?"
        val selectionArgs = arrayOf(id)
        return db.query(TABLE_NAME, null, selection, selectionArgs, null, null, null)
    }

    fun deleteDataById(id: String) {
        val db = writableDatabase
        val deleteQuery = "DELETE FROM $TABLE_NAME WHERE _id = $id"
        db.execSQL(deleteQuery)
        db.close()
    }

    fun updateData(
        id: String,
        title: String,
        amount: String,
        description: String,

        ) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_TITLE, title)
            put(COLUMN_AMOUNT, amount)
            put(COLUMN_DESCRIPTION, description)
        }
        val selection = "_id = ?"
        val selectionArgs = arrayOf(id)
        db.update(TABLE_NAME, values, selection, selectionArgs)
        db.close()
    }

    fun deleteAllData() {
        val db = writableDatabase
        db.delete(TABLE_NAME, null, null)
        db.close()
    }

}
