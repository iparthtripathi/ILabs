package com.pratik.iiits

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.pratik.iiits.Models.Post
import com.pratik.iiits.notes.Adapters.PostsAdapter

class PostsFragment : Fragment(R.layout.fragment_posts) {

    private lateinit var firestoreDb: FirebaseFirestore
    private lateinit var posts: MutableList<Post>
    private lateinit var postsAdapter: PostsAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        posts = mutableListOf()
        postsAdapter = PostsAdapter(requireContext(), posts)

        val rvPosts = view.findViewById<RecyclerView>(R.id.rvPosts)
        rvPosts.adapter = postsAdapter
        rvPosts.layoutManager = LinearLayoutManager(requireContext())

        firestoreDb = FirebaseFirestore.getInstance()

        fetchPosts()
    }

    private fun fetchPosts() {
        val postsReference = firestoreDb.collection("posts")
            .limit(20)
            .orderBy("creation_time_ms", Query.Direction.DESCENDING)
        postsReference.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w(TAG, "Listen failed.", e)
                return@addSnapshotListener
            }
            if (snapshot != null && !snapshot.isEmpty) {
                val postList = snapshot.toObjects(Post::class.java)
                posts.clear()
                posts.addAll(postList)
                postsAdapter.notifyDataSetChanged()
                Log.d(TAG, "Posts fetched successfully: ${posts.size} posts")
            } else {
                Log.d(TAG, "No posts found")
            }
        }
    }
}
