package com.pratik.iiits

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.pratik.iiits.Adapters.CategoryAdapter

class CategoriesActivity : AppCompatActivity() {
    private lateinit var categoriesRecyclerView: RecyclerView
    private val categoriesList = listOf("Sports", "Music", "Art", "Science", "Technology")
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_categories)

        categoriesRecyclerView = findViewById(R.id.categories_recycler_view)
        categoriesRecyclerView.layoutManager = LinearLayoutManager(this)
        categoriesRecyclerView.adapter = CategoryAdapter(categoriesList, ::openCategory)
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        checkIfAdmin()

        findViewById<Button>(R.id.group_requests_button).setOnClickListener {
            val intent = Intent(this, GroupRequestsActivity::class.java)
            startActivity(intent)
        }

    }

    private fun checkIfAdmin() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val userId = currentUser.uid
            firestore.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val postInIIIT = document.getString("postinIIIT")
                        Log.e(ContentValues.TAG, postInIIIT.toString())
                        if (postInIIIT == "Admin"||postInIIIT=="Council") {
                            findViewById<Button>(R.id.group_requests_button).visibility = Button.VISIBLE
                        } else {
                            findViewById<Button>(R.id.group_requests_button).visibility = Button.GONE
                        }
                    }
                }
                .addOnFailureListener { e ->
                    Log.e(ContentValues.TAG, "Error fetching user details: $e")
                    findViewById<Button>(R.id.group_requests_button).visibility = Button.GONE
                }
        }
    }

    private fun openCategory(category: String) {
        val intent = Intent(this, GroupsListActivity::class.java)
        intent.putExtra("category", category)
        startActivity(intent)
    }
}
