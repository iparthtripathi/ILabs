package com.pratik.iiits

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.app.NotificationCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.pratik.iiits.Models.Post
import com.pratik.iiits.notes.Adapters.PostsAdapter
import com.pratik.iiits.utils.PostUtils

class PostsFragment : Fragment(R.layout.fragment_posts) {

    private lateinit var firestoreDb: FirebaseFirestore
    private lateinit var posts: MutableList<Post>
    private lateinit var postsAdapter: PostsAdapter
    private var previousPosts: List<Post> = listOf()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        posts = mutableListOf()
        postsAdapter = PostsAdapter(requireContext(), posts)

        val rvPosts = view.findViewById<RecyclerView>(R.id.rvPosts)
        rvPosts.adapter = postsAdapter
        rvPosts.layoutManager = LinearLayoutManager(requireContext())

        firestoreDb = FirebaseFirestore.getInstance()

        // Load previous posts from SharedPreferences
        previousPosts = PostUtils.loadPosts(requireContext())
        Log.d(TAG, "Loaded previous posts: ${previousPosts.size}")

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

                // Compare the number of posts and send notification if there's a change
                if (posts.size != previousPosts.size) {
                    if (isAdded && context != null) {
                        sendNotification("New posts available!")
                    }
                    previousPosts = posts
                    // Save updated posts to SharedPreferences
                    PostUtils.savePosts(requireContext(), previousPosts)
                    Log.d(TAG, "Saved new posts: ${previousPosts.size}")
                }
            } else {
                Log.d(TAG, "No posts found")
            }
        }
    }

    private fun sendNotification(messageBody: String) {
        context?.let { context ->
            val intent = Intent(context, EventsActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            }
            val pendingIntent = PendingIntent.getActivity(context, 0, intent,
                PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE)

            val channelId = "post_updates_channel"
            val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val notificationBuilder = NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.logo_splash_chatapp)
                .setContentTitle("Post Update")
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent)

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            // Since Android Oreo notification channel is needed.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(channelId, "Post Updates", NotificationManager.IMPORTANCE_DEFAULT)
                notificationManager.createNotificationChannel(channel)
            }

            notificationManager.notify(0, notificationBuilder.build())
        } ?: Log.e(TAG, "Context is null, cannot send notification")
    }
}
