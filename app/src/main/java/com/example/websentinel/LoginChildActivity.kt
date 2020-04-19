package com.example.websentinel


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.tasks.OnCompleteListener
import kotlin.math.sign


class LoginChildActivity : AppCompatActivity() {

    private lateinit var mGoogleSignInClient:GoogleSignInClient
    private lateinit var signOutButton: SignInButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_child)

        val gso: GoogleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        // Build a GoogleSignInClient with the options specified by gso.

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        val acct = GoogleSignIn.getLastSignedInAccount(this)

        if (acct != null) {
            //do stuff
        }
        signOutButton = findViewById(R.id.sign_out_button)
        signOutButton.setOnClickListener{onClick(signOutButton)}
    }

    private fun signOut() {
        mGoogleSignInClient.signOut()
            .addOnCompleteListener(this, {
                if (it.isSuccessful){
                    intent = Intent(this, InitialActivity::class.java)
                    Toast.makeText(this,"$TAG :)",Toast.LENGTH_SHORT).show() //TODO: delete it

                    startActivity(intent)
                }
                else
                    Log.w(TAG,"Sign out task failed")
            })
    }

    private fun revokeAccess() { //revokes the access from the google account
        mGoogleSignInClient.revokeAccess()
            .addOnCompleteListener(this, {
                // ...
            })
    }

    fun onClick(v: View) {
        when (v.getId()) {
            R.id.sign_out_button -> signOut()
        }
    }


    companion object{
        private val TAG = "LoginChildActivity"
    }
}
