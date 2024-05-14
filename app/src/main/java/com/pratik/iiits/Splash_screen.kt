package com.pratik.iiits

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.pratik.iiits.R
import com.google.firebase.auth.FirebaseAuth
import android.content.Intent
import android.os.Handler
import android.widget.Toast
import com.pratik.iiits.login_user
import com.pratik.iiits.MainActivity

class Splash_screen : AppCompatActivity() {
    var h: Handler? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        h = Handler()
        h!!.postDelayed({
            val auth = FirebaseAuth.getInstance()
            if (auth.currentUser == null) startActivity(Intent(this@Splash_screen, login_user::class.java))
            else {
                //Toast.makeText(this@Splash_screen,auth.currentUser.toString(),Toast.LENGTH_SHORT).show()
                startActivity(Intent(this@Splash_screen, MainActivity::class.java))
            }
        }, 6000)
    }
}