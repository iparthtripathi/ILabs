package com.pratik.iiits

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.SignInMethodQueryResult
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.pratik.iiits.Models.UserModel

class login_user : AppCompatActivity() {

    private var validmail = false
    private val emailPattern = Regex("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+")
    private lateinit var passwordbox: EditText
    private lateinit var emailbox: EditText
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var loadingProgressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_user)

        emailbox = findViewById(R.id.emailbox)
        passwordbox = findViewById(R.id.passwordbox)
        loadingProgressBar = findViewById(R.id.loadingProgressBar)
        configureright()
        emailbox.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun afterTextChanged(editable: Editable) {
                validmail = emailPattern.matches(emailbox.text.toString().trim())
                Log.i("check", validmail.toString())
                checkvalidmail()
            }
        })
        auth = FirebaseAuth.getInstance()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    fun signin(view: View) {
        val signInIntent = googleSignInClient.signInIntent
        launcher.launch(signInIntent)
    }

    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            handleresults(task)
        }
    }

    private fun handleresults(task: Task<GoogleSignInAccount>) {
        if (task.isSuccessful) {
            val account: GoogleSignInAccount? = task.result
            if (account != null) {
                updateUi(account)
            } else {
                Toast.makeText(this, "Google Sign-In account is null", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Google Sign-In failed: ${task.exception}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateUi(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        auth.fetchSignInMethodsForEmail(account.email.toString())
            .addOnCompleteListener { task ->
                Log.i("user2", FirebaseAuth.getInstance().uid.toString())
                Log.i("user", task.result.signInMethods.toString())
                val isNewUser = FirebaseAuth.getInstance().uid.isNullOrBlank()
                if (isNewUser) {
                    Toast.makeText(this, "Account Doesn't Exist! Please Register.", Toast.LENGTH_SHORT).show()
                } else {
                    auth.signInWithCredential(credential).addOnCompleteListener {
                        if (it.isSuccessful) {
                            login(account, null)
                        } else {
                            Toast.makeText(this, "${it.exception}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
    }

    fun login_with_email(view: View) {
        // Hide the sign-in button
        view.visibility = View.GONE

        // Show loading bar
        loadingProgressBar.visibility = View.VISIBLE

        val mail = emailbox.text.toString()
        val pass = passwordbox.text.toString()
        if (mail.isNotEmpty() && pass.isNotEmpty() && validmail) {
            auth.signInWithEmailAndPassword(mail, pass)
                .addOnCompleteListener(this) { task ->
                    // Hide loading bar
                    loadingProgressBar.visibility = View.INVISIBLE

                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        login(null, user)
                    } else {
                        // Handle sign-in failure
                        when (task.exception) {
                            // Handle specific exceptions
                            is FirebaseAuthInvalidUserException -> {
                                Toast.makeText(baseContext, "User does not exist.", Toast.LENGTH_SHORT).show()
                            }
                            is FirebaseAuthInvalidCredentialsException -> {
                                Toast.makeText(baseContext, "Invalid email or password.", Toast.LENGTH_SHORT).show()
                            }
                            is FirebaseAuthUserCollisionException -> {
                                Toast.makeText(baseContext, "Email is already in use.", Toast.LENGTH_SHORT).show()
                            }
                            else -> {
                                Toast.makeText(baseContext, "Authentication failed.", Toast.LENGTH_SHORT).show()
                            }
                        }
                        // Show the sign-in button again
                        view.visibility = View.VISIBLE
                    }
                }
        } else {
            Toast.makeText(this, "Please enter all credentials properly!", Toast.LENGTH_SHORT).show()
            // Hide loading bar
            loadingProgressBar.visibility = View.INVISIBLE
            // Show the sign-in button again
            view.visibility = View.VISIBLE
        }
    }


    private fun login(account: GoogleSignInAccount?, user: FirebaseUser?) {
        startActivity(Intent(this@login_user, MainActivity::class.java))
    }



    fun configureright(){
        passwordbox.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        addRighDrawable(passwordbox,R.drawable.eye)
        addRighDrawable(emailbox,R.drawable.ic_baseline_assignment_late_24)
        passwordbox.onRightDrawableClicked {
            if (passwordbox.inputType== InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD){
                //Show password
                addRighDrawable(passwordbox,R.drawable.closed_eye)
                passwordbox.inputType= InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD

            }
            else {
                //hide password
                addRighDrawable(passwordbox,R.drawable.eye)
                passwordbox.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            }
        }
    }

    fun open_register(view: View) {
        val intent = Intent(this,Register::class.java)
        startActivity(intent)
    }


    fun checkvalidmail() {
        if (validmail){
            addRighDrawable(emailbox,R.drawable.ic_baseline_assignment_turned_in_24)
        }
        else addRighDrawable(emailbox,R.drawable.ic_baseline_assignment_late_24)
    }

    //Functions to add right drawable
    private fun addRighDrawable(editText: EditText, drawableid : Int) {
        val cancel = ContextCompat.getDrawable(this,drawableid)
        cancel?.setBounds(-10,0, 80, 70)
        editText.setCompoundDrawables(null, null, cancel, null)
    }
    @SuppressLint("ClickableViewAccessibility")
    fun EditText.onRightDrawableClicked(onClicked: (view: EditText) -> Unit) {
        this.setOnTouchListener { v, event ->
            var hasConsumed = false
            if (v is EditText) {
                if (event.x >= v.width - v.totalPaddingRight) {
                    if (event.action == MotionEvent.ACTION_UP) {
                        onClicked(this)
                    }
                    hasConsumed = true
                }
            }
            hasConsumed
        }
    }

}