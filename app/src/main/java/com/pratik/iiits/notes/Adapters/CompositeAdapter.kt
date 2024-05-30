package com.pratik.iiits.notes.Adapters

import android.content.Context
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.pratik.iiits.Models.Post
import com.pratik.iiits.Models.Poll
import com.pratik.iiits.databinding.ItemPollBinding
import com.pratik.iiits.databinding.ItemPostsBinding
import com.squareup.picasso.Picasso

class CompositeAdapter(
    private val context: Context,
    private val posts: List<Post>,
    private val polls: List<Poll>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val VIEW_TYPE_POST = 0
    private val VIEW_TYPE_POLL = 1

    override fun getItemViewType(position: Int): Int {
        return if (position < posts.size) VIEW_TYPE_POST else VIEW_TYPE_POLL
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_POST) {
            val binding = ItemPostsBinding.inflate(LayoutInflater.from(context), parent, false)
            PostViewHolder(binding)
        } else {
            val binding = ItemPollBinding.inflate(LayoutInflater.from(context), parent, false)
            PollViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == VIEW_TYPE_POST) {
            (holder as PostViewHolder).bind(posts[position])
        } else {
            (holder as PollViewHolder).bind(polls[position - posts.size])
        }
    }

    override fun getItemCount(): Int {
        return posts.size + polls.size
    }

    inner class PostViewHolder(private val binding: ItemPostsBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(post: Post) {
            binding.name.text = post.user?.name ?: "NULL"
            binding.description.text = post.description ?: "No description"
            val imageUrl = "https://bit.ly/3T5Uk5W"
            Picasso.get().load(imageUrl).into(binding.userimage)
            binding.time.text = DateUtils.getRelativeTimeSpanString(post.creation_time_ms)
            Picasso.get().load(post.image_url).into(binding.postImage)
        }
    }

    inner class PollViewHolder(private val binding: ItemPollBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(poll: Poll) {
            binding.tvPollQuestion.text = poll.question
            binding.llPollOptions.removeAllViews()
            poll.options.forEach { option ->
                val optionView = TextView(context).apply {
                    text = option
                    textSize = 14f
                    setTextColor(ContextCompat.getColor(context, android.R.color.black))
                }
                binding.llPollOptions.addView(optionView)
            }
        }
    }
}
