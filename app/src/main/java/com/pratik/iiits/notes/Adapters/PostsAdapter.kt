package com.pratik.iiits.notes.Adapters

import android.content.Context
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pratik.iiits.Models.Post
import com.pratik.iiits.databinding.ItemPostsBinding
import com.squareup.picasso.Picasso

class PostsAdapter(private val context: Context, private val posts: List<Post>) : RecyclerView.Adapter<PostsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemPostsBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount() = posts.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(posts[position])
    }

    inner class ViewHolder(private val binding: ItemPostsBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(post: Post) {
            binding.name.text = post.user?.name ?: "NULL"
            binding.description.text = post.description ?: "No description"
            val imageUrl = "https://bit.ly/3T5Uk5W"
            Picasso.get().load(imageUrl).into(binding.userimage)
            binding.time.text= DateUtils.getRelativeTimeSpanString(post.creation_time_ms)
            Picasso.get().load(post.image_url).into(binding.postImage)
        }
    }
}
