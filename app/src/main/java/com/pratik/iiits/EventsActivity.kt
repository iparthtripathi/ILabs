package com.pratik.iiits

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.pratik.iiits.Models.Post
import com.pratik.iiits.Models.Poll
import com.pratik.iiits.notes.Adapters.CompositeAdapter

class EventsActivity : AppCompatActivity() {
    private lateinit var firestoreDb: FirebaseFirestore
    private lateinit var posts: MutableList<Post>
    private lateinit var polls: MutableList<Poll>
    private lateinit var compositeAdapter: CompositeAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_events)

        posts = mutableListOf()
        polls = mutableListOf()

        compositeAdapter = CompositeAdapter(this, posts, polls)

        val rvPosts = findViewById<RecyclerView>(R.id.rvPosts)
        rvPosts.adapter = compositeAdapter
        rvPosts.layoutManager = LinearLayoutManager(this)

        firestoreDb = FirebaseFirestore.getInstance()

        fetchPosts()
        fetchPolls()
    }

    private fun fetchPosts() {
        val postsReference = firestoreDb.collection("posts")
            .limit(20)
            .orderBy("creation_time_ms", Query.Direction.DESCENDING)
        postsReference.addSnapshotListener { snapshot, e ->
            if (e != null || snapshot == null) {
                Log.w(TAG, "Listen failed.", e)
                return@addSnapshotListener
            }
            val postList = snapshot.toObjects(Post::class.java)
            posts.clear()
            posts.addAll(postList)
            compositeAdapter.notifyDataSetChanged()
            for (post in postList) {
                Log.d(TAG, "Post: $post")
            }
        }
    }

    private fun fetchPolls() {
        val pollsReference = firestoreDb.collection("polls")
            .limit(20)
            .orderBy("creation_time_ms", Query.Direction.DESCENDING)
        pollsReference.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.e(TAG, "Error fetching polls", e)
                return@addSnapshotListener
            }

            if (snapshot != null) {
                val pollList = snapshot.toObjects(Poll::class.java)
                polls.clear()
                polls.addAll(pollList)
                compositeAdapter.notifyDataSetChanged()
                for (poll in pollList) {
                    Log.d(TAG, "Poll: $poll")
                }
            } else {
                Log.d(TAG, "Polls snapshot is null")
            }
        }
    }


    fun createPost(view: View) {
        val intent = Intent(this, ChooseActivity::class.java)
        startActivity(intent)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finishAffinity()
    }
}
