package com.spendwise

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class DatabaseHelper(context: Context, userMail: String) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    private val DATABASE_TABLE_EXPENSE =
        Constants.TABLE_EXPENSE + "_" + removeDotsAndNumbers(userMail)
    private val DATABASE_TABLE_CREDIT =
        Constants.TABLE_CREDIT + "_" + removeDotsAndNumbers(userMail)

    companion object {

        private const val DATABASE_NAME = "ExpenseDetails.db"

        private const val DATABASE_VERSION = 1
        private const val COLUMN_USER_MAIL = "mail"
        private const val COLUMN_TITLE = "title"
        private const val COLUMN_DATE = "date"
        private const val COLUMN_CATEGORY = "category"
        private const val COLUMN_P_METHOD = "p_method"
        private const val COLUMN_AMOUNT = "amount"
        private const val COLUMN_AMOUNT_SIGN = "amount_sign"
        private const val COLUMN_BLOB_RECEIPT = "BlobDataReceipt"
        private const val COLUMN_BLOB_TYPE = "BlobDataType"
        private const val COLUMN_HAS_FILE = "HasFile"
        private const val COLUMN_FILE_PATH = "FilePath"
        private const val COLUMN_DESCRIPTION = "description"
        private const val COLUMN_DUE_DATE = "due_date"
        private const val COLUMN_RATE_OF_INTEREST = "rate_of_interest"
    }

    override fun onCreate(db: SQLiteDatabase) {


        val createTableQueryCREDIT: String =
            "CREATE TABLE " + "$DATABASE_TABLE_CREDIT " + "(_id INTEGER PRIMARY KEY AUTOINCREMENT, " + "$COLUMN_TITLE TEXT, " + "$COLUMN_USER_MAIL TEXT, " + "$COLUMN_DATE TEXT, " + "$COLUMN_DUE_DATE TEXT, " + "$COLUMN_RATE_OF_INTEREST TEXT, " + "$COLUMN_AMOUNT TEXT, " + "$COLUMN_AMOUNT_SIGN TEXT, " + "$COLUMN_DESCRIPTION TEXT)"

        val createTableQuery: String =
            "CREATE TABLE " + "$DATABASE_TABLE_EXPENSE " + "(_id INTEGER PRIMARY KEY AUTOINCREMENT, " + "$COLUMN_TITLE TEXT, " + "$COLUMN_USER_MAIL TEXT, " + "$COLUMN_DATE TEXT, " + "$COLUMN_CATEGORY TEXT, " + "$COLUMN_P_METHOD TEXT, " + "$COLUMN_AMOUNT TEXT, " + "$COLUMN_AMOUNT_SIGN TEXT, " + "$COLUMN_BLOB_RECEIPT BLOB, " + "$COLUMN_BLOB_TYPE TEXT," + "$COLUMN_HAS_FILE TEXT," + "$COLUMN_FILE_PATH TEXT," + "$COLUMN_DESCRIPTION TEXT)"


        db.execSQL(createTableQuery)
        db.execSQL(createTableQueryCREDIT)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Perform necessary actions when upgrading the database
        // For simplicity, we'll drop the table and recreate it
        val dropTableQuery = "DROP TABLE IF EXISTS $DATABASE_TABLE_EXPENSE"
        db.execSQL(dropTableQuery)
        val dropTableQueryCredit = "DROP TABLE IF EXISTS $DATABASE_TABLE_CREDIT"
        db.execSQL(dropTableQueryCredit)
        onCreate(db)
    }

    fun removeDotsAndNumbers(email: String): String {
        val pattern = Regex("[.0-9@]")
        return pattern.replace(email, "")
    }

    fun insertDataExpense(
        mail: String,
        title: String,
        date: String,
        category: String,
        p_method: String,
        amount: String,
        signAmount: String,
        blobFileData: ByteArray,
        blobType: String,
        description: String,
        hasFile: String,
        filePath: String
    ) {
        val db = writableDatabase
        val values = ContentValues().apply {

            put(COLUMN_USER_MAIL, mail)
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
            put(COLUMN_AMOUNT_SIGN, signAmount)

        }
        db.insert(DATABASE_TABLE_EXPENSE, null, values)
        db.close()
    }

    fun insertDataExpense(
        mail: String,

        title: String,
        date: String,
        category: String,
        p_method: String,
        amount: String,
        signAmount: String

    ) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_USER_MAIL, mail)

            put(COLUMN_TITLE, title)
            put(COLUMN_DATE, date)
            put(COLUMN_CATEGORY, category)
            put(COLUMN_P_METHOD, p_method)
            put(COLUMN_AMOUNT, amount)
            put(COLUMN_AMOUNT_SIGN, signAmount)
        }
        db.insert(DATABASE_TABLE_EXPENSE, null, values)
        db.close()
    }

    fun insertDataExpense(
        mail: String,

        title: String,
        date: String,
        category: String,
        p_method: String,
        amount: String,
        signAmount: String,
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
            put(COLUMN_AMOUNT_SIGN, signAmount)
            put(COLUMN_USER_MAIL, mail)

        }
        db.insert(DATABASE_TABLE_EXPENSE, null, values)
        db.close()
    }

    fun insertDataExpense(
        mail: String,

        title: String,
        date: String,
        category: String,
        p_method: String,
        amount: String,
        signAmount: String,
        description: String
    ) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_TITLE, title)
            put(COLUMN_DATE, date)
            put(COLUMN_CATEGORY, category)
            put(COLUMN_P_METHOD, p_method)
            put(COLUMN_AMOUNT, amount)
            put(COLUMN_AMOUNT_SIGN, signAmount)
            put(COLUMN_USER_MAIL, mail)

            put(COLUMN_DESCRIPTION, description)
        }
        db.insert(DATABASE_TABLE_EXPENSE, null, values)
        db.close()
        Log.e("database", "successfull")
    }


    fun retrieveExpensesData(): Cursor? {
        val db = readableDatabase
        return db.query(DATABASE_TABLE_EXPENSE, null, null, null, null, null, null)
    }

    fun retrieveCreditsData(): Cursor? {
        val db = readableDatabase
        return db.query(DATABASE_TABLE_CREDIT, null, null, null, null, null, null)
    }

    fun retrieveExpenseDataById(id: String): Cursor? {
        val db = readableDatabase
        val selection = "_id = ?"
        val selectionArgs = arrayOf(id)
        return db.query(DATABASE_TABLE_EXPENSE, null, selection, selectionArgs, null, null, null)
    }

    fun retrieveCreditDataById(id: String): Cursor? {
        val db = readableDatabase
        val selection = "_id = ?"
        val selectionArgs = arrayOf(id)
        return db.query(DATABASE_TABLE_CREDIT, null, selection, selectionArgs, null, null, null)
    }

    fun deleteExpenseDataById(id: String) {
        val db = writableDatabase
        val deleteQuery = "DELETE FROM $DATABASE_TABLE_EXPENSE WHERE _id = $id"
        db.execSQL(deleteQuery)
        db.close()
    }

    fun deleteCreditDataById(id: String) {
        val db = writableDatabase
        val deleteQuery = "DELETE FROM $DATABASE_TABLE_CREDIT WHERE _id = $id"
        db.execSQL(deleteQuery)
        db.close()
    }

    fun updateExpenseData(
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
        db.update(DATABASE_TABLE_EXPENSE, values, selection, selectionArgs)
        db.close()
    }

    fun deleteAllData() {
        val db = writableDatabase
        db.delete(DATABASE_TABLE_EXPENSE, null, null)
        db.delete(DATABASE_TABLE_CREDIT, null, null)
        db.close()
    }

    fun getExpensesAmountsForLast6Months(): Cursor {
        val db = readableDatabase
        val query =
            "SELECT SUM($COLUMN_AMOUNT) AS total_amount, strftime('%m/%Y', date(substr($COLUMN_DATE, 7, 4) || '-' || substr($COLUMN_DATE, 4, 2) || '-' || substr($COLUMN_DATE, 1, 2))) AS month_year " + "FROM $DATABASE_TABLE_EXPENSE " + "WHERE date(substr($COLUMN_DATE, 7, 4) || '-' || substr($COLUMN_DATE, 4, 2) || '-' || substr($COLUMN_DATE, 1, 2)) >= date('now', '-6 months') " + "GROUP BY month_year " + "ORDER BY month_year ASC"
        return db.rawQuery(query, null)
    }

    fun insertDataCredit(
        creditRateOfInterest: String,
        mail: String,
        creditSign: String,
        creditTitle: String,
        creditDate: String,
        creditAmount: String,
        creditDueDate: String,
        creditDescription: String
    ) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_TITLE, creditTitle)
            put(COLUMN_DATE, creditDate)
            put(COLUMN_DUE_DATE, creditDueDate)
            put(COLUMN_AMOUNT, creditAmount)
            put(COLUMN_USER_MAIL, mail)
            put(COLUMN_AMOUNT_SIGN, creditSign)
            put(COLUMN_DESCRIPTION, creditDescription)
            put(COLUMN_RATE_OF_INTEREST, creditRateOfInterest)
        }
        db.insert(DATABASE_TABLE_CREDIT, null, values)
        db.close()
        Log.e("database", "successfull")

    }

    fun insertDataCredit(
        creditRateOfInterest: String,
        mail: String,
        creditSign: String,
        creditTitle: String,
        creditDate: String,
        creditAmount: String,
        creditDueDate: String,
    ) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_TITLE, creditTitle)
            put(COLUMN_DATE, creditDate)
            put(COLUMN_DUE_DATE, creditDueDate)
            put(COLUMN_AMOUNT, creditAmount)
            put(COLUMN_USER_MAIL, mail)
            put(COLUMN_AMOUNT_SIGN, creditSign)
            put(COLUMN_RATE_OF_INTEREST, creditRateOfInterest)

        }
        db.insert(DATABASE_TABLE_CREDIT, null, values)
        db.close()
        Log.e("database", "successfull")

    }


}
