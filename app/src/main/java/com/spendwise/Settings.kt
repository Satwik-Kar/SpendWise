package com.spendwise

import android.content.Intent
import android.os.Bundle
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.card.MaterialCardView


class Settings : AppCompatActivity() {
    private lateinit var switch: SwitchCompat
    private lateinit var logoutView: MaterialCardView
    private lateinit var deleteData: MaterialCardView
    private lateinit var changeCountry: MaterialCardView
    private lateinit var backup: MaterialCardView
    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this@Settings, HomeActivity::class.java))
        finishAffinity()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        switch = findViewById(R.id.switchAppLock)
        logoutView = findViewById(R.id.setting_log_out)
        deleteData = findViewById(R.id.setting_delete_data)
        changeCountry = findViewById(R.id.setting_change_country)
        backup = findViewById(R.id.setting_backup)
        val stateOfAppLock = getSharedPreferences(
            "credentials", MODE_PRIVATE
        ).getBoolean("setting_app_lock", true)
        switch.isChecked = stateOfAppLock
        switch.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                // Switch is ON
                getSharedPreferences(
                    "credentials", MODE_PRIVATE
                ).edit().putBoolean("setting_app_lock", true).apply()
                Toast.makeText(this@Settings, "App Lock enabled", Toast.LENGTH_SHORT).show()

            } else {
                // Switch is OFF
                getSharedPreferences(
                    "credentials", MODE_PRIVATE
                ).edit().putBoolean("setting_app_lock", false).apply()
                Toast.makeText(this@Settings, "App Lock disabled", Toast.LENGTH_SHORT).show()

            }
        }
        logoutView.setOnClickListener {
            val alert = AlertDialog.Builder(this@Settings)
            alert.setTitle("Log out").setMessage("Are you sure to log out?")
            alert.setPositiveButton(
                "Log out"


            ) { _, _ ->
                val gso =
                    GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail()
                        .build()
                val googleSignInClient = GoogleSignIn.getClient(this, gso)
                googleSignInClient.signOut()
                getSharedPreferences("credentials", MODE_PRIVATE).edit()
                    .putBoolean("hasAccountLoggedIn", false).apply()
                finishAffinity()
                Toast.makeText(this@Settings, "Logged Out", Toast.LENGTH_LONG).show()


            }

            alert.setNegativeButton("No") { _, _ ->

                Toast.makeText(
                    this@Settings, "Logging out cancelled", Toast.LENGTH_SHORT
                ).show()

            }
            alert.create().show()


        }
        changeCountry.setOnClickListener {


            val view = layoutInflater.inflate(R.layout.countryview, null)
            val alert = AlertDialog.Builder(this@Settings)
            alert.setView(view)

            alert.setPositiveButton(
                "Change"


            ) { _, _ ->

                val getSelectedItem =
                    view.findViewById<Spinner>(R.id.spinner).selectedItem.toString()

                getSharedPreferences("credentials", MODE_PRIVATE).edit()
                    .putString("country", getSelectedItem).apply()

                Toast.makeText(
                    this@Settings, "Default country updated to $getSelectedItem", Toast.LENGTH_LONG
                ).show()


            }
            alert.setNegativeButton("Cancel") { _, _ ->

                Toast.makeText(
                    this@Settings, "Cancelled", Toast.LENGTH_SHORT
                ).show()

            }
            alert.setTitle("Choose a country")
            alert.setMessage("Choose a country to use the currency for expenses.")
            alert.create().show()

        }
        deleteData.setOnClickListener {
            val alert = AlertDialog.Builder(this@Settings)
            alert.setTitle("Delete App data")
                .setMessage("Alert! All app data including your login information and expenses will be deleted and cannot be undone.Are you sure?")
            alert.setPositiveButton(
                "Delete"


            ) { _, _ ->

                val gso =
                    GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail()
                        .build()
                val googleSignInClient = GoogleSignIn.getClient(this, gso)
                googleSignInClient.signOut()
                val email =
                    getSharedPreferences("credentials", MODE_PRIVATE).getString("email", null)!!
                val tableName = removeDotsAndNumbers(email)
                DatabaseHelper(this@Settings, email).deleteAllData()
                Toast.makeText(this@Settings, "All app data deleted", Toast.LENGTH_SHORT).show()
                getSharedPreferences("credentials", MODE_PRIVATE).edit().clear().apply()
                finishAffinity()
                Toast.makeText(this@Settings, "Deleted App data", Toast.LENGTH_LONG).show()


            }
            alert.setNegativeButton("No") { _, _ ->

                Toast.makeText(
                    this@Settings, "Deleting data cancelled", Toast.LENGTH_SHORT
                ).show()

            }
            alert.create().show()


        }
        backup.setOnClickListener {


            val intent = Intent(this@Settings, BackupActivity::class.java)
            startActivity(intent)

        }


    }

    private fun removeDotsAndNumbers(email: String): String {
        val pattern = Regex("[.0-9@]")
        return pattern.replace(email, "")
    }
}