package com.pratik.iiits

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import com.pratik.iiits.databinding.ActivityOtpVerifyBinding
import com.hbb20.CountryCodePicker
import java.util.concurrent.TimeUnit

class otpVerify : AppCompatActivity() {

    private lateinit var binding: ActivityOtpVerifyBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    private var verificationId: String? = null
    private var resendToken: PhoneAuthProvider.ForceResendingToken? = null
    private val countdownDuration = 60000L // 1 minute in milliseconds

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOtpVerifyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.countryCodePicker.registerCarrierNumberEditText(binding.phonebox)
        binding.phonebox.requestFocus()

        auth = FirebaseAuth.getInstance()
        binding.sendOtpButton.setOnClickListener { sendVerificationCode() }
        binding.verifyOtpButton.setOnClickListener { verifyCode() }
        binding.resendOtpButton.setOnClickListener { resendVerificationCode() }

        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                signInWithPhoneAuthCredential(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                Toast.makeText(this@otpVerify, "Verification Failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }

            override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                this@otpVerify.verificationId = verificationId
                this@otpVerify.resendToken = token
                Toast.makeText(this@otpVerify, "OTP Sent", Toast.LENGTH_SHORT).show()
                startCountdown()
            }
        }
    }

    private fun verifyCode() {
        val code = binding.otpbox.text.toString().trim()
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
        val phoneNumber = binding.phonebox.text.toString().trim()
        val fullPhoneNumber = binding.countryCodePicker.fullNumberWithPlus

        if (phoneNumber.isNotEmpty()) {
            binding.otpbox.visibility = View.VISIBLE
            binding.otpInstructionText.visibility = View.VISIBLE
            binding.sendOtpButton.visibility = View.GONE
            binding.verifyOtpButton.visibility = View.VISIBLE
            binding.resendOtpButton.visibility = View.VISIBLE
            binding.otpLayout.visibility = View.VISIBLE
            binding.textInputLayout.visibility = View.GONE
            binding.instructionText.visibility = View.GONE
            binding.otpInstructionText.visibility = View.VISIBLE

            val options = PhoneAuthOptions.newBuilder(auth)
                .setPhoneNumber(fullPhoneNumber)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(callbacks)
                .build()
            PhoneAuthProvider.verifyPhoneNumber(options)
        } else {
            Toast.makeText(this, "Please enter a valid phone number", Toast.LENGTH_SHORT).show()
            binding.otpbox.visibility = View.GONE
            binding.otpInstructionText.visibility = View.GONE
            binding.sendOtpButton.visibility = View.VISIBLE
            binding.verifyOtpButton.visibility = View.GONE
            binding.resendOtpButton.visibility = View.GONE
        }
    }

    private fun resendVerificationCode() {
        val phoneNumber = binding.phonebox.text.toString().trim()
        val fullPhoneNumber = binding.countryCodePicker.fullNumberWithPlus

        if (phoneNumber.isNotEmpty() && resendToken != null) {
            val options = PhoneAuthOptions.newBuilder(auth)
                .setPhoneNumber(fullPhoneNumber)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(callbacks)
                .setForceResendingToken(resendToken!!)
                .build()
            PhoneAuthProvider.verifyPhoneNumber(options)
        } else {
            Toast.makeText(this, "Cannot resend OTP at the moment", Toast.LENGTH_SHORT).show()
        }
    }

    private fun startCountdown() {
        binding.resendOtpButton.isEnabled = false
        binding.resendOtpButton.visibility = View.GONE
        object : CountDownTimer(countdownDuration, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsRemaining = millisUntilFinished / 1000
                binding.countdownText.visibility = View.VISIBLE
                binding.countdownText.text = "Resend OTP in $secondsRemaining seconds"
            }

            override fun onFinish() {
                binding.resendOtpButton.isEnabled = true
                binding.resendOtpButton.visibility = View.VISIBLE
                binding.countdownText.visibility = View.GONE
            }
        }.start()
    }
}
