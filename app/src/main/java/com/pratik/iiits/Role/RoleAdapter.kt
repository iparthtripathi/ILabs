package com.pratik.iiits.Role

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pratik.iiits.R

class RoleAdapter(private val roleList: List<RoleRequest>) : RecyclerView.Adapter<RoleAdapter.RoleViewHolder>() {

    class RoleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val roleNameTextView: TextView = itemView.findViewById(R.id.role_name_text_view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoleViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_role, parent, false)
        return RoleViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RoleViewHolder, position: Int) {
        val role = roleList[position]
        holder.roleNameTextView.text = role.roleName
    }

    override fun getItemCount(): Int {
        return roleList.size
    }
}
