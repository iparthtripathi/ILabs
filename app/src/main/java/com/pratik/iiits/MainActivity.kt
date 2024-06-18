package com.pratik.iiits

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.pratik.iiits.Marketplace.BuyActivity
import com.pratik.iiits.chatapp.ChatAppHome
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import java.util.Calendar


class MainActivity : AppCompatActivity() {
    private var mAuth: FirebaseAuth? = null
    private var daytime: TextView? = null
    lateinit var database: FirebaseDatabase
    lateinit var auth: FirebaseAuth
    lateinit var userimage: CircleImageView
    lateinit var hiusername : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        hook()
        mAuth = FirebaseAuth.getInstance()
        daytime = findViewById(R.id.daytime)

        setGreeting()

        val ref: DatabaseReference = database.getReference().child("users").child(auth.uid.toString())
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Picasso.get().load(snapshot.child("imageUri").value.toString()).into(userimage)
                hiusername.text = "Hi "+snapshot.child("name").value.toString()
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun setGreeting() {

        val calendar = Calendar.getInstance()
        val hour = calendar[Calendar.HOUR_OF_DAY]
        var greeting = ""

        greeting = if (hour >= 6 && hour < 12) {
            "Good morning!"
        } else if (hour >= 12 && hour < 18) {
            "Good afternoon!"
        } else if (hour >= 18 && hour < 22) {
            "Good evening!"
        } else {
            "Good night!"
        }

        daytime!!.text = greeting
    }

    fun openEvents(view: View?) {
        startActivity(Intent(this@MainActivity,EventsActivity::class.java))
    }


    fun openCalender(view: View) {
        startActivity(Intent(this@MainActivity,Calender::class.java))

    }

    fun opennotesactivity(view: View?) {
        val intent = Intent(this@MainActivity, NotesActivity::class.java)
        startActivity(intent)
    }

    fun openprofile(view: View?) {
        startActivity(Intent(this@MainActivity,ProfilePage::class.java).putExtra("authuid",auth.uid.toString()).putExtra("self",true))
    }

    fun openchats(view: View?) {
        val intent = Intent(this@MainActivity, ChatAppHome::class.java)
        startActivity(intent)
    }

    fun openGroups(view: View?) {
        val intent = Intent(this@MainActivity, CategoriesActivity::class.java)
        startActivity(intent)
    }

    fun openMarket(view: View?) {
        val intent = Intent(this@MainActivity, BuyActivity::class.java)
        startActivity(intent)
    }

    fun openMaps(view: View?) {
        val intent = Intent(this@MainActivity, MapsActivity::class.java)
        startActivity(intent)
    }

    private fun hook() {
        database =  FirebaseDatabase.getInstance()
        auth = FirebaseAuth.getInstance()
        userimage= findViewById(R.id.userimage)
        hiusername = findViewById(R.id.username)
    }
}