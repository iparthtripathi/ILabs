package com.pratik.iiits

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pratik.iiits.Adapters.CategoryAdapter

class CategoriesActivity : AppCompatActivity() {
    private lateinit var categoriesRecyclerView: RecyclerView
    private val categoriesList = listOf("Sports", "Music", "Art", "Science", "Technology")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_categories)

        categoriesRecyclerView = findViewById(R.id.categories_recycler_view)
        categoriesRecyclerView.layoutManager = LinearLayoutManager(this)
        categoriesRecyclerView.adapter = CategoryAdapter(categoriesList, ::openCategory)

    }

    private fun openCategory(category: String) {
        val intent = Intent(this, GroupsListActivity::class.java)
        intent.putExtra("category", category)
        startActivity(intent)
    }
}
