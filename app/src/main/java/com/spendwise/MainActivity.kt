package com.spendwise

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.telecom.Call
import android.util.Log
import android.widget.Toast
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInResult


class MainActivity : Activity() {
    private val RC_SIGN_IN: Int = 1024
    private var mGoogleSignInClient: GoogleSignInClient? = null

    private val SPLASH_TIMEOUT: Long = 2000
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val signedIn = getSharedPreferences("credentials", MODE_PRIVATE).getBoolean("hasAccountLoggedIn",false)
        if (signedIn){
            Handler().postDelayed({
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
                finish()
            }, SPLASH_TIMEOUT)
        }else{
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build()
            mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
            signIn()
        }






    }

    private fun signIn() {
        val signInIntent = mGoogleSignInClient!!.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            handleSignInResult(result)
        }else{
            Toast.makeText(this@MainActivity,"Try again later!",Toast.LENGTH_LONG).show()
        }
    }

    private fun handleSignInResult(result: GoogleSignInResult) {
        Log.d("authentication", "handleSignInResult:" + result.isSuccess)

        if (result.isSuccess) {

            val acct = result.signInAccount!!
            getSharedPreferences("credentials", MODE_PRIVATE).edit().putBoolean("hasAccountLoggedIn",true)
                .putString("name",acct.displayName).putString("uid",acct.id)
                .putString("email",acct.email).putString("photo_url", acct.photoUrl?.toString()).apply()
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()


        } else {

            Toast.makeText(this, "Sign-in failed:"+  result.status.status, Toast.LENGTH_SHORT).show()
            getSharedPreferences("credentials", MODE_PRIVATE).edit().putBoolean("hasAccountLoggedIn",false).apply()
        }
    }

}