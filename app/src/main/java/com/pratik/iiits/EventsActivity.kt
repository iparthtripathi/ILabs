package com.pratik.iiits

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.pratik.iiits.R

class EventsActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_events)

        bottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_posts -> {
                    loadFragment(PostsFragment())
                    true
                }
                R.id.navigation_polls -> {
                    loadFragment(PollsFragment())
                    true
                }
                else -> false
            }
        }

        // Load the default fragment
        bottomNavigationView.selectedItemId = R.id.navigation_posts
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }
    fun createPost(view: View) {
        val intent = Intent(this, ChooseActivity::class.java)
        startActivity(intent)
    }

}
