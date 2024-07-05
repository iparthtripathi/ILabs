package com.pratik.iiits.Timetable

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.pratik.iiits.NotesActivity
import com.pratik.iiits.R
import java.util.*

class ScheduleActivity : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var ugSpinner: Spinner
    private lateinit var branchSpinner: Spinner
    private lateinit var sectionSpinner: Spinner
    private lateinit var calendarView: CalendarView
    private lateinit var recyclerView: RecyclerView
    private lateinit var scheduleAdapter: ScheduleAdapter
    private val scheduleList: MutableList<ClassSchedule> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schedule)

        firestore = FirebaseFirestore.getInstance()

        ugSpinner = findViewById(R.id.ugSpinner)
        branchSpinner = findViewById(R.id.branchSpinner)
        sectionSpinner = findViewById(R.id.sectionSpinner)
        calendarView = findViewById(R.id.calendarView)
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        scheduleAdapter = ScheduleAdapter(scheduleList)
        recyclerView.adapter = scheduleAdapter

        loadUGPrograms()
        findViewById<ImageButton>(R.id.addSchedule).setOnClickListener{
            val intent = Intent(this@ScheduleActivity, Adminentry::class.java)
            startActivity(intent)
        }

        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val calendar = Calendar.getInstance()
            calendar.set(year, month, dayOfMonth)
            val dayOfWeek = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault())
            fetchSchedule(dayOfWeek)
        }
    }

    private fun loadUGPrograms() {
        firestore.collection("ugPrograms").get().addOnSuccessListener { documents ->
            val ugPrograms = documents.map { it.id }
            val ugAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, ugPrograms)
            ugAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            ugSpinner.adapter = ugAdapter

            ugSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                    val selectedUG = ugPrograms[position]
                    loadBranches(selectedUG)
                }

                override fun onNothingSelected(parent: AdapterView<*>) {}
            }
        }
    }

    private fun loadBranches(ugProgram: String) {
        firestore.collection("ugPrograms").document(ugProgram).collection("branches").get().addOnSuccessListener { documents ->
            val branches = documents.map { it.id }
            val branchAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, branches)
            branchAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            branchSpinner.adapter = branchAdapter

            branchSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                    val selectedBranch = branches[position]
                    loadSections(ugProgram, selectedBranch)
                }

                override fun onNothingSelected(parent: AdapterView<*>) {}
            }
        }
    }

    private fun loadSections(ugProgram: String, branch: String) {
        firestore.collection("ugPrograms").document(ugProgram).collection("branches").document(branch).collection("sections").get().addOnSuccessListener { documents ->
            val sections = documents.map { it.id }
            val sectionAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, sections)
            sectionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            sectionSpinner.adapter = sectionAdapter

            sectionSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                    // No specific action needed here for now
                }

                override fun onNothingSelected(parent: AdapterView<*>) {}
            }
        }
    }

    private fun fetchSchedule(dayOfWeek: String?) {
        val selectedUG = ugSpinner.selectedItem?.toString()
        val selectedBranch = branchSpinner.selectedItem?.toString()
        val selectedSection = sectionSpinner.selectedItem?.toString()

        if (selectedUG.isNullOrBlank() || selectedBranch.isNullOrBlank() || selectedSection.isNullOrBlank() || dayOfWeek.isNullOrBlank()) {
            Toast.makeText(this, "Please select UG program, branch, section, and a date.", Toast.LENGTH_SHORT).show()
            return
        }

        firestore.collection("schedules")
            .document(selectedUG)
            .collection(selectedBranch)
            .document(selectedSection)
            .collection("repeatingSchedules")
            .document(dayOfWeek)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val daySchedule = document.toObject(DaySchedule::class.java)
                    scheduleList.clear()
                    scheduleList.addAll(daySchedule?.schedules ?: emptyList())
                    scheduleAdapter.notifyDataSetChanged()
                } else {
                    scheduleList.clear()
                    scheduleAdapter.notifyDataSetChanged()
                    Toast.makeText(this, "No Schedule Found", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to fetch schedule: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
