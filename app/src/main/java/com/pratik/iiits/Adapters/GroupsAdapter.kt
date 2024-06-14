package com.pratik.iiits.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.pratik.iiits.Models.Group
import com.pratik.iiits.R

class GroupsAdapter(
    private val groupsList: List<Group>,
    private val onItemClick: (Group) -> Unit
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
        private val groupInfo: TextView = itemView.findViewById(R.id.group_info)

        fun bind(group: Group) {
            groupName.text = group.name

            // Load group profile image
            Glide.with(itemView.context)
                .load(R.drawable.img5) // assuming 'profileImageUrl' is a field in your Group model
                .placeholder(R.drawable.placeholder_image) // placeholder image
                .into(groupProfileImage)

            itemView.setOnClickListener {
                onItemClick(group)
            }
        }
    }
}
