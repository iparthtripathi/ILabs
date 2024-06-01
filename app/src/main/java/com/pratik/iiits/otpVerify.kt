package com.pratik.iiits

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.hbb20.CountryCodePicker
import java.util.concurrent.TimeUnit

class otpVerify : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var phonebox: EditText
    private lateinit var otpbox: EditText
    private lateinit var sendOtpButton: Button
    private lateinit var verifyOtpButton: Button
    private lateinit var instructionText: TextView
    private lateinit var otpInstructionText: TextView
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    private var verificationId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp_verify)

        phonebox = findViewById(R.id.phonebox)
        otpbox = findViewById(R.id.otpbox)
        sendOtpButton = findViewById(R.id.sendOtpButton)
        verifyOtpButton = findViewById(R.id.verifyOtpButton)
        instructionText = findViewById(R.id.instructionText)
        otpInstructionText = findViewById(R.id.otpInstructionText)

        auth = FirebaseAuth.getInstance()
        sendOtpButton.setOnClickListener { sendVerificationCode() }
        verifyOtpButton.setOnClickListener { verifyCode() }

        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                signInWithPhoneAuthCredential(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                Toast.makeText(this@otpVerify, "Verification Failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }

            override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                this@otpVerify.verificationId = verificationId
                Toast.makeText(this@otpVerify, "OTP Sent", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun verifyCode() {
        val code = otpbox.text.toString().trim()
        if (code.isNotEmpty() && verificationId != null) {
            val credential = PhoneAuthProvider.getCredential(verificationId!!, code)
            signInWithPhoneAuthCredential(credential)
        } else {
            Toast.makeText(this, "Please enter the OTP", Toast.LENGTH_SHORT).show()
        }
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                val user = task.result?.user
                startActivity(Intent(this, login_user::class.java))
            } else {
                val message = if (task.exception is FirebaseAuthInvalidCredentialsException) {
                    "Invalid OTP"
                } else {
                    "Sign in failed: ${task.exception?.message}"
                }
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun sendVerificationCode() {
        val phoneNumber = phonebox.text.toString().trim()
        val fullPhoneNumber = "+91$phoneNumber"

        if (phoneNumber.isNotEmpty()) {
            otpbox.visibility = View.VISIBLE
            otpInstructionText.visibility = View.VISIBLE
            sendOtpButton.visibility = View.GONE
            verifyOtpButton.visibility = View.VISIBLE
            findViewById<TextInputLayout>(R.id.otpLayout).visibility=View.VISIBLE
            findViewById<TextInputLayout>(R.id.textInputLayout).visibility=View.GONE
            findViewById<TextView>(R.id.instructionText).visibility=View.GONE
            findViewById<TextView>(R.id.otpInstructionText).visibility=View.VISIBLE

            val options = PhoneAuthOptions.newBuilder(auth)
                .setPhoneNumber(fullPhoneNumber)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(callbacks)
                .build()
            PhoneAuthProvider.verifyPhoneNumber(options)
        } else {
            Toast.makeText(this, "Please enter a valid phone number", Toast.LENGTH_SHORT).show()
            otpbox.visibility = View.GONE
            otpInstructionText.visibility = View.GONE
            sendOtpButton.visibility = View.VISIBLE
            verifyOtpButton.visibility = View.GONE
        }
    }
}
