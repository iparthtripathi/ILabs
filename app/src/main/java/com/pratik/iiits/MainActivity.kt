package com.pratik.iiits

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.pratik.iiits.Marketplace.HomeActivity
import com.pratik.iiits.R
import com.pratik.iiits.chatapp.ChatAppHome
import com.pratik.iiits.chatapp.Splash_chatapp
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class MainActivity : AppCompatActivity() {

    lateinit var database: FirebaseDatabase
    lateinit var auth: FirebaseAuth
    lateinit var userimage: CircleImageView
    lateinit var hiusername : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        hook()


        val ref:  DatabaseReference = database.getReference().child("users").child(auth.uid.toString())
        ref.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                Picasso.get().load(snapshot.child("imageUri").value.toString()).into(userimage)
                hiusername.text = "Hi "+snapshot.child("name").value.toString()
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })




    }

    fun events(view: View) {
        startActivity(Intent(this@MainActivity,EventsActivity::class.java))
    }

    fun openMaps(view: View) {
        startActivity(Intent(this@MainActivity,MapsActivity::class.java))
    }

    fun openCalender(view: View) {
        startActivity(Intent(this@MainActivity,Calender::class.java))

    }
    fun openGroups(view: View) {
        startActivity(Intent(this@MainActivity,GroupsActivity::class.java))
    }

    fun opennotesactivity(view: View) {
        startActivity(Intent(this@MainActivity,NotesActivity::class.java))
    }



    fun openchats(view: View) {
        startActivity(Intent(this@MainActivity, ChatAppHome::class.java))

    }

    fun openMarket(view: View) {
        startActivity(Intent(this@MainActivity, HomeActivity::class.java))
    }

    private fun hook() {
        database =  FirebaseDatabase.getInstance()
        auth = FirebaseAuth.getInstance()
        userimage= findViewById(R.id.userimage)
        hiusername = findViewById(R.id.username)
    }

    fun openprofile(view: View) {

        startActivity(Intent(this@MainActivity,ProfilePage::class.java).putExtra("authuid",auth.uid.toString()).putExtra("self",true))

    }


}