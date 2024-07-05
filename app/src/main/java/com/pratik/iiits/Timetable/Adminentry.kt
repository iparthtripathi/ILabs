package com.pratik.iiits.Timetable

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TimePicker
import android.widget.Toast
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.pratik.iiits.R

class Adminentry : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var dayOfWeekSpinner: Spinner
    private lateinit var timePicker: TimePicker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_adminentry)

        firestore = FirebaseFirestore.getInstance()

        val ugEditText: EditText = findViewById(R.id.ugEditText)
        val branchEditText: EditText = findViewById(R.id.branchEditText)
        val sectionEditText: EditText = findViewById(R.id.sectionEditText)
        val subjectEditText: EditText = findViewById(R.id.subjectEditText)
        val roomEditText: EditText = findViewById(R.id.roomEditText)
        dayOfWeekSpinner = findViewById(R.id.dayOfWeekSpinner)
        timePicker = findViewById(R.id.timePicker)

        val addUgButton: Button = findViewById(R.id.addUgButton)
        val addBranchButton: Button = findViewById(R.id.addBranchButton)
        val addSectionButton: Button = findViewById(R.id.addSectionButton)
        val submitButton: Button = findViewById(R.id.submitButton)

        // Populate day of the week spinner
        val daysOfWeek = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")
        val dayOfWeekAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, daysOfWeek)
        dayOfWeekAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        dayOfWeekSpinner.adapter = dayOfWeekAdapter

        addUgButton.setOnClickListener {
            val ugProgram = ugEditText.text.toString()
            if (ugProgram.isNotEmpty()) {
                firestore.collection("ugPrograms").document(ugProgram).set(mapOf("name" to ugProgram))
                    .addOnSuccessListener {
                        Toast.makeText(this, "UG Program Added", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Failed to Add UG Program", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(this, "Please enter UG Program name", Toast.LENGTH_SHORT).show()
            }
        }

        addBranchButton.setOnClickListener {
            val branch = branchEditText.text.toString()
            val ugProgram = ugEditText.text.toString()
            if (branch.isNotEmpty() && ugProgram.isNotEmpty()) {
                firestore.collection("ugPrograms").document(ugProgram).collection("branches").document(branch).set(mapOf("name" to branch))
                    .addOnSuccessListener {
                        Toast.makeText(this, "Branch Added", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Failed to Add Branch", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(this, "Please enter Branch name and select UG Program", Toast.LENGTH_SHORT).show()
            }
        }

        addSectionButton.setOnClickListener {
            val section = sectionEditText.text.toString()
            val branch = branchEditText.text.toString()
            val ugProgram = ugEditText.text.toString()
            if (section.isNotEmpty() && branch.isNotEmpty() && ugProgram.isNotEmpty()) {
                firestore.collection("ugPrograms").document(ugProgram).collection("branches").document(branch).collection("sections").document(section).set(mapOf("name" to section))
                    .addOnSuccessListener {
                        Toast.makeText(this, "Section Added", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Failed to Add Section", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(this, "Please enter Section name, Branch name, and select UG Program", Toast.LENGTH_SHORT).show()
            }
        }

        submitButton.setOnClickListener {
            val subject = subjectEditText.text.toString().trim()
            val room = roomEditText.text.toString().trim()
            val dayOfWeek = dayOfWeekSpinner.selectedItem.toString()
            val ugProgram = ugEditText.text.toString().trim()
            val branch = branchEditText.text.toString().trim()
            val section = sectionEditText.text.toString().trim()

            val hour = if (Build.VERSION.SDK_INT >= 23) timePicker.hour else timePicker.currentHour
            val minute = if (Build.VERSION.SDK_INT >= 23) timePicker.minute else timePicker.currentMinute
            val time = String.format("%02d:%02d", hour, minute)

            // Validate inputs
            if (subject.isEmpty() || room.isEmpty() || dayOfWeek.isEmpty() || ugProgram.isEmpty() || branch.isEmpty() || section.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Create a ClassSchedule object
            val classSchedule = ClassSchedule(subject, time, room, dayOfWeek)

            // Firestore path to the repeatingSchedules collection
            val scheduleRef = firestore.collection("schedules")
                .document(ugProgram)
                .collection(branch)
                .document(section)
                .collection("repeatingSchedules")
                .document(dayOfWeek)

            // Check if the schedule for the dayOfWeek already exists
            scheduleRef.get().addOnSuccessListener { document ->
                if (document.exists()) {
                    // If the document exists, update the array field with the new schedule
                    scheduleRef.update("schedules", FieldValue.arrayUnion(classSchedule))
                        .addOnSuccessListener {
                            Toast.makeText(this, "Schedule Added", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Failed to Add Schedule: ${it.message}", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    // If the document doesn't exist, create a new document with the initial schedule
                    scheduleRef.set(mapOf("schedules" to mutableListOf(classSchedule)))
                        .addOnSuccessListener {
                            Toast.makeText(this, "Schedule Added", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Failed to Add Schedule: ${it.message}", Toast.LENGTH_SHORT).show()
                        }
                }
            }.addOnFailureListener {
                Toast.makeText(this, "Failed to check existing schedule: ${it.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
