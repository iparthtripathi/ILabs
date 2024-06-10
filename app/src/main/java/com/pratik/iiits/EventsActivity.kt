package com.pratik.iiits

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth


class EventsActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView
    private var mAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_events)


        mAuth = FirebaseAuth.getInstance();
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

        val currentUser = mAuth!!.currentUser
        if (currentUser != null && "admin123@gmail.com" != currentUser.email) {
            findViewById<FloatingActionButton>(R.id.fab).setVisibility(View.GONE)
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
