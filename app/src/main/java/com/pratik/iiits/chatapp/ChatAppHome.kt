package com.pratik.iiits.chatapp

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.pratik.iiits.Models.UserModel
import com.pratik.iiits.R
import com.pratik.iiits.Register
import com.pratik.iiits.notes.Adapters.UserlistAdapter

class ChatAppHome : AppCompatActivity() {



    lateinit var auth: FirebaseAuth
    lateinit var mainUserRecyclerView: RecyclerView
    lateinit var adapter:UserlistAdapter
    lateinit var database:FirebaseDatabase
    lateinit var usersArrayList: ArrayList<UserModel>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_app_home)


        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        usersArrayList  = ArrayList<UserModel>()

        val databaseReference = database.reference.child("users")
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                usersArrayList.clear()
                for (datasnapshot:DataSnapshot in snapshot.children){
//                    Toast.makeText(this@ChatAppHome,datasnapshot.value.toString(),Toast.LENGTH_SHORT).show()
                    val users:UserModel = UserModel(
                        datasnapshot.child("uid").value.toString(),
                        datasnapshot.child("name").value.toString(),
                        datasnapshot.child("email").value.toString(),
                        datasnapshot.child("postinIIIT").value.toString(),
                        datasnapshot.child("imageUri").value.toString(),
                        datasnapshot.child("status").value.toString()

                    )
                    if (users.uid.toString() != auth.uid.toString())
                        usersArrayList.add(users)
                }

                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })

        mainUserRecyclerView= findViewById(R.id.mainUserRecyclerView)
        mainUserRecyclerView.layoutManager= LinearLayoutManager(this)
        adapter = UserlistAdapter(this@ChatAppHome, usersArrayList)
        mainUserRecyclerView.adapter = adapter

        if (auth.currentUser==null)startActivity(Intent(this@ChatAppHome , Register::class.java) )
    }

    fun logout(view: View) {
        val dailog : BottomSheetDialog = BottomSheetDialog(this@ChatAppHome,R.style.BottomSheetStyle)
        dailog.setContentView(R.layout.logout_dailog)
        dailog.show()
        val yesBtn = dailog.findViewById<TextView>(R.id.yesbtn)
        val noBtn = dailog.findViewById<TextView>(R.id.nobtn)

        yesBtn?.setOnClickListener {
            //Exit from Chat section
            dailog.dismiss()
        }
        noBtn?.setOnClickListener {
          dailog.dismiss()
        }

    }
}