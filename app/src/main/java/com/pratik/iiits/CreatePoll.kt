package com.pratik.iiits

import android.content.ContentValues
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.pratik.iiits.Models.Poll
import com.pratik.iiits.Models.UserModel

class CreatePoll : AppCompatActivity() {
    private lateinit var pollQuestion: TextInputEditText
    private lateinit var optionsContainer: LinearLayout
    private var user1: UserModel? = null
    private lateinit var firestoreDb: FirebaseFirestore
    private lateinit var submitButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_poll)

        submitButton = findViewById(R.id.createPollBtn)
        pollQuestion = findViewById(R.id.pollQuestion)
        optionsContainer = findViewById(R.id.optionsContainer)
        firestoreDb = FirebaseFirestore.getInstance()

        firestoreDb.collection("users")
            .document(FirebaseAuth.getInstance().currentUser?.uid as String)
            .get()
            .addOnSuccessListener { userSnapshot ->
                user1 = userSnapshot.toObject(UserModel::class.java)
                Log.d(ContentValues.TAG, "Username: $user1")
            }

        // Add initial option fields
        addOptionField()
        addOptionField()
    }

    fun addOption(view: View) {
        addOptionField()
    }

    private fun addOptionField() {
        val textInputLayout = TextInputLayout(this)
        textInputLayout.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            setMargins(0, 0, 0, 16)
        }

        // Set hint text color to black
        textInputLayout.defaultHintTextColor = ColorStateList.valueOf(ContextCompat.getColor(this, android.R.color.black))

        textInputLayout.hint = "Option ${optionsContainer.childCount + 1}"
        textInputLayout.setBoxBackgroundMode(TextInputLayout.BOX_BACKGROUND_OUTLINE)

        val textInputEditText = TextInputEditText(this)
        textInputEditText.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        // Set text color to black
        textInputEditText.setTextColor(ContextCompat.getColor(this, android.R.color.black))

        // Set background tint to black
        textInputEditText.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this, android.R.color.black))

        textInputLayout.addView(textInputEditText)
        optionsContainer.addView(textInputLayout)
    }




    fun submitPoll(view: View) {
        submitButton.isEnabled = false
        val question = pollQuestion.text.toString()

        if (question.isEmpty()) {
            Toast.makeText(this, "Please enter a question", Toast.LENGTH_SHORT).show()
            submitButton.isEnabled = true
            return
        }

        val options = mutableListOf<String>()
        for (i in 0 until optionsContainer.childCount) {
            val textInputLayout = optionsContainer.getChildAt(i) as TextInputLayout
            val optionText = textInputLayout.editText?.text.toString()
            if (optionText.isNotEmpty()) {
                options.add(optionText)
            }
        }

        if (options.size < 2) {
            Toast.makeText(this, "Please enter at least two options", Toast.LENGTH_SHORT).show()
            submitButton.isEnabled = true
            return
        }

        if (user1 == null) {
            Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show()
            submitButton.isEnabled = true
            return
        }

        val poll = Poll(
            question,
            options,
            System.currentTimeMillis(),
            user1
        )

        firestoreDb.collection("polls").add(poll)
            .addOnCompleteListener { pollCreationTask ->
                submitButton.isEnabled = true
                if (!pollCreationTask.isSuccessful) {
                    Log.e("Exception", "Failed to save poll", pollCreationTask.exception)
                } else {
                    val eventsIntent = Intent(this, EventsActivity::class.java)
                    startActivity(eventsIntent)
                    finish()
                }
            }
    }
}
