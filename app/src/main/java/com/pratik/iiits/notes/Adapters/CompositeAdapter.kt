package com.pratik.iiits.notes.Adapters

import android.content.Context
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pratik.iiits.Models.Post
import com.pratik.iiits.Models.Poll

class CompositeAdapter(
    private val context: Context,
    private val posts: List<Post>?,
    private val polls: List<Poll>?
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_POST = 1
        private const val VIEW_TYPE_POLL = 2
    }

    override fun getItemViewType(position: Int): Int {
        return if (position < (posts?.size ?: 0)) VIEW_TYPE_POST else VIEW_TYPE_POLL
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_POST && posts != null) {
            PostsAdapter(context, posts).createViewHolder(parent, viewType)
        } else if (viewType == VIEW_TYPE_POLL && polls != null) {
            PollAdapter(context, polls).createViewHolder(parent, viewType)
        } else {
            // Error case, handle appropriately or return a default ViewHolder
            // Log the error
            Log.e("CompositeAdapter", "Error creating ViewHolder: Null data list or invalid view type")
            // You can return a default ViewHolder or throw an exception
            // For simplicity, returning a default ViewHolder
            object : RecyclerView.ViewHolder(View(context)) {}
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == VIEW_TYPE_POST && posts != null) {
            (holder as? PostsAdapter.ViewHolder)?.bind(posts[position])
        } else if (getItemViewType(position) == VIEW_TYPE_POLL && polls != null) {
            (holder as? PollAdapter.ViewHolder)?.bind(polls[position - (posts?.size ?: 0)])
        }
    }

    override fun getItemCount(): Int {
        return (posts?.size ?: 0) + (polls?.size ?: 0)
    }
}
