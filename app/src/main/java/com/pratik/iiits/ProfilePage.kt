package com.pratik.iiits

import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.pratik.iiits.chatapp.ChatScreen
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class ProfilePage : AppCompatActivity() {

    lateinit var auth: FirebaseAuth
    lateinit var database: FirebaseDatabase
    lateinit var authid:String
    lateinit var userimage: CircleImageView
    lateinit var username:TextView
    lateinit var useremail:TextView
    lateinit var userpost:TextView
    lateinit var useremail2:TextView
    lateinit var bio:TextView
    lateinit var btn1:ImageButton
    lateinit var uri:String
    private lateinit var googleSignInClient: GoogleSignInClient


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_page)

        hook()

        authid = intent.getStringExtra("authuid").toString()
        val self = intent.getBooleanExtra("self",false)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this,gso)

        val uri1 = intent.data
        if (uri1 != null) {
            val parameters: List<String> = uri1.pathSegments
            authid = parameters[parameters.size - 1]
        }
        val ref = database.getReference("users").child(authid)

        if (self){
            btn1.setImageResource(R.drawable.ic_baseline_edit_note_24)
        }
        else{
            btn1.setImageResource(R.drawable.ic_baseline_message_24)

        }
        ref.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                uri = snapshot.child("imageUri").value.toString()
                Picasso.get().load(uri).into(userimage)
                username.text = snapshot.child("name").value.toString()
                userpost.text = snapshot.child("postinIIIT").value.toString()
                useremail.text = snapshot.child("email").value.toString()
                useremail2.text = snapshot.child("email").value.toString()
                bio.text = snapshot.child("status").value.toString()

            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    fun logout(view: View) {
        val dailog : BottomSheetDialog = BottomSheetDialog(this@ProfilePage,R.style.BottomSheetStyle)
        dailog.setContentView(R.layout.logout_dailog)
        dailog.show()
        val yesBtn = dailog.findViewById<TextView>(R.id.yesbtn)
        val noBtn = dailog.findViewById<TextView>(R.id.nobtn)

        yesBtn?.setOnClickListener {
            // Sign out from Firebase Authentication
            auth.signOut()

            // Sign out from Google Sign-In (if applicable)
            googleSignInClient.signOut().addOnCompleteListener(this) {
                // Redirect the user to the login screen or perform any other necessary actions
                startActivity(Intent(this@ProfilePage, login_user::class.java))
                finish() // Close the current activity to prevent the user from returning to it using the back button
            }
            dailog.dismiss()
        }
        noBtn?.setOnClickListener {
            dailog.dismiss()
        }

    }

    private fun hook() {
        auth=  FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        userimage= findViewById(R.id.profileimage)
        username= findViewById(R.id.profilename)
        useremail= findViewById(R.id.profileemail)
        useremail2= findViewById(R.id.profileemail2)
        userpost= findViewById(R.id.profilepost)
        bio= findViewById(R.id.statusbio)
        btn1 = findViewById(R.id.meassgeoredit)
    }

    fun mailme(view: View?) {}
    fun closeprofile(view: View?) {
        finish()
    }
    fun shareprofile(view: View?) {

        val emailIntent = Intent(Intent.ACTION_SEND)

        emailIntent.putExtra(Intent.EXTRA_SUBJECT, ""+intent.getStringExtra("name").toString())
        val ss = Html.fromHtml("Find my User Details through this link, https://www.iiits.in/$authid")
        emailIntent.putExtra(Intent.EXTRA_TEXT,ss.toString() )
        emailIntent.type = "text/plain";
        startActivity(Intent.createChooser(emailIntent, "Send to friend"))

    }
    fun messageme(view: View?) {
        val intent = Intent(this@ProfilePage, ChatScreen::class.java)
        intent.putExtra("name", username.text.toString())
        intent.putExtra("ReciverImage", uri)
        intent.putExtra("uid", authid)
        startActivity(intent)

    }
}