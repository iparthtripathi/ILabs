package com.pratik.iiits.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.pratik.iiits.Models.Group
import com.pratik.iiits.R

class GroupsAdapter(
    private val groupsList: List<Group>,
    private val onItemClick: (Group) -> Unit,
    private val onItemLongClick: (Group) -> Unit,
    private val isYourGroupsList: Boolean
) : RecyclerView.Adapter<GroupsAdapter.GroupViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_group, parent, false)
        return GroupViewHolder(view)
    }

    override fun onBindViewHolder(holder: GroupViewHolder, position: Int) {
        val group = groupsList[position]
        holder.bind(group)
    }

    override fun getItemCount(): Int = groupsList.size

    inner class GroupViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val groupProfileImage: ImageView = itemView.findViewById(R.id.group_profile_image)
        private val groupName: TextView = itemView.findViewById(R.id.group_name)
        private val lastMessage: TextView = itemView.findViewById(R.id.last_message)
        private val unreadIndicator: View = itemView.findViewById(R.id.unread_indicator)

        fun bind(group: Group) {
            groupName.text = group.name
            lastMessage.text = group.lastMessage

            // Load group profile image
            Glide.with(itemView.context)
                .load(R.drawable.img5) // assuming 'profileImageUrl' is a field in your Group model
                .placeholder(R.drawable.placeholder_image) // placeholder image
                .into(groupProfileImage)

            // Check for unread messages
            val currentUser = FirebaseAuth.getInstance().currentUser
            if (currentUser != null && group.unreadMessages[currentUser.uid] == true) {
                unreadIndicator.visibility = View.VISIBLE
            } else {
                unreadIndicator.visibility = View.GONE
            }

            itemView.setOnClickListener {
                onItemClick(group)
            }

            itemView.setOnLongClickListener {
                onItemLongClick(group)
                true
            }
        }
    }
}
