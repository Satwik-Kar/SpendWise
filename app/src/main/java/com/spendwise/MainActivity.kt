package com.spendwise

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.biometric.BiometricPrompt.PromptInfo
import androidx.core.content.ContextCompat
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInResult


class MainActivity : AppCompatActivity() {
    private val RC_SIGN_IN: Int = 1024
    private var mGoogleSignInClient: GoogleSignInClient? = null
    private lateinit var biometricManager: BiometricManager
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var biometricPromptInfo: PromptInfo
    private val SPLASH_TIMEOUT: Long = 2000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val stateAppLock =
            getSharedPreferences("credentials", MODE_PRIVATE).getBoolean("setting_app_lock", true)
        if (stateAppLock) {
            val bm = BiometricManager.from(this)
            when (bm.canAuthenticate()) {

                BiometricManager.BIOMETRIC_SUCCESS -> {
                }

                BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {

                }

                BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                }

                BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                }
            }
            val executor = ContextCompat.getMainExecutor(this)
            biometricPrompt = BiometricPrompt(
                this@MainActivity,
                executor,
                object : BiometricPrompt.AuthenticationCallback() {
                    override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                        super.onAuthenticationError(errorCode, errString)
                        Toast.makeText(this@MainActivity, errString, Toast.LENGTH_SHORT).show()

                        finishAffinity()


                    }

                    override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                        super.onAuthenticationSucceeded(result)
                        Handler().postDelayed({
                            val intent = Intent(this@MainActivity, HomeActivity::class.java)
                            startActivity(intent)
                            finish()
                        }, SPLASH_TIMEOUT)


                    }

                })
            biometricPromptInfo = PromptInfo.Builder().setTitle("SpendWise Security")
                .setDescription("Use biometric sensors to move inside SpendWise")
                .setDeviceCredentialAllowed(true).build()
            val signedIn = getSharedPreferences("credentials", MODE_PRIVATE).getBoolean(
                "hasAccountLoggedIn", false
            )
            if (signedIn) {
                biometricPrompt.authenticate(biometricPromptInfo)
            } else {
                val gso =
                    GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail()
                        .build()
                mGoogleSignInClient = GoogleSignIn.getClient(this@MainActivity, gso)
                signIn()

            }
        } else {
            val signedIn = getSharedPreferences("credentials", MODE_PRIVATE).getBoolean(
                "hasAccountLoggedIn", false
            )
            if (signedIn) {
                Handler().postDelayed({
                    val intent = Intent(this@MainActivity, HomeActivity::class.java)
                    startActivity(intent)
                    finish()
                }, SPLASH_TIMEOUT)
            } else {
                val gso =
                    GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail()
                        .build()
                mGoogleSignInClient = GoogleSignIn.getClient(this@MainActivity, gso)
                signIn()
            }
        }


    }


    private fun signIn() {
        val signInIntent = mGoogleSignInClient!!.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data!!)
            handleSignInResult(result!!)
        } else {
            Toast.makeText(this@MainActivity, "Try again later!", Toast.LENGTH_LONG).show()
        }
    }

    private fun handleSignInResult(result: GoogleSignInResult) {
        Log.d("authentication", "handleSignInResult:" + result.isSuccess)

        if (result.isSuccess) {

            val acct = result.signInAccount!!
            getSharedPreferences("credentials", MODE_PRIVATE).edit()
                .putBoolean("hasAccountLoggedIn", true).putString("name", acct.displayName)
                .putString("uid", acct.id).putString("email", acct.email)
                .putString("photo_url", acct.photoUrl?.toString()).apply()
            val view = layoutInflater.inflate(R.layout.countryview, null)
            val builder = AlertDialog.Builder(this@MainActivity)
            builder.setTitle("Choose Country")
            builder.setMessage("Choose the default country")
            builder.setView(view)
            builder.setPositiveButton("Next") { _, _ ->
                val response = view.findViewById<Spinner>(R.id.spinner).selectedItem
                getSharedPreferences("credentials", MODE_PRIVATE).edit()
                    .putString("country", response.toString())
                    .putString("currency", Constants.getMap()[response.toString()]).apply()
                startActivity(Intent(this@MainActivity, HomeActivity::class.java))
                finishAffinity()
            }
            builder.show()


        } else {

            Toast.makeText(this, "Sign-in failed:" + result.status.status, Toast.LENGTH_SHORT)
                .show()
            getSharedPreferences("credentials", MODE_PRIVATE).edit()
                .putBoolean("hasAccountLoggedIn", false).apply()
        }
    }

}