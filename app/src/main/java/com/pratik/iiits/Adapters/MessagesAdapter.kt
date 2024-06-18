package com.pratik.iiits.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.database.*
import com.pratik.iiits.Models.Message
import com.pratik.iiits.Models.UserModel
import com.pratik.iiits.R
import java.text.SimpleDateFormat
import java.util.*

class MessagesAdapter(private val context: Context, private val messagesList: List<Message>) :
    RecyclerView.Adapter<MessagesAdapter.ViewHolder>() {

    private val userMap = mutableMapOf<String, UserModel?>()
    private val MESSAGE_TYPE_TEXT = 1
    private val MESSAGE_TYPE_IMAGE = 2

    inner class TextMessageViewHolder(itemView: View) : ViewHolder(itemView) {
        val messageText: TextView = itemView.findViewById(R.id.message_text)
    }

    inner class ImageMessageViewHolder(itemView: View) : ViewHolder(itemView) {
        val messageImage: ImageView = itemView.findViewById(R.id.message_image)
    }

    override fun getItemViewType(position: Int): Int {
        return if (messagesList[position].imageUrl != null) {
            MESSAGE_TYPE_IMAGE
        } else {
            MESSAGE_TYPE_TEXT
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(
            if (viewType == MESSAGE_TYPE_TEXT) R.layout.item_text_message else R.layout.item_image_message,
            parent, false
        )
        return if (viewType == MESSAGE_TYPE_TEXT) TextMessageViewHolder(view) else ImageMessageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val message = messagesList[position]

        // Check if user details are already fetched
        if (userMap.containsKey(message.senderId)) {
            val user = userMap[message.senderId]
            holder.userName.text = user?.name ?: ""  // Use null-check operator
            Glide.with(context).load(user?.imageUri ?: "").into(holder.senderImage)
        } else {
            fetchUserDetails(message.senderId, holder)
        }

        if (holder is TextMessageViewHolder) {
            val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
            val time = sdf.format(Date(message.timestamp))
            holder.messageTime.text = time
            holder.messageText.text = message.message
        } else if (holder is ImageMessageViewHolder) {
            val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
            val time = sdf.format(Date(message.timestamp))
            holder.messageTime.text = time
            Glide.with(holder.messageImage.context)
                .load(message.imageUrl)
                .into(holder.messageImage)
        }
    }

    private fun fetchUserDetails(userId: String, holder: ViewHolder?) {
        val userReference = FirebaseDatabase.getInstance().reference.child("users").child(userId)
        userReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(UserModel::class.java)
                if (user != null) {
                    userMap[userId] = user
                    notifyDataSetChanged()  // Notify the adapter that the data has changed
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle possible errors
            }
        })
    }

    override fun getItemCount(): Int {
        return messagesList.size
    }

    sealed class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userName: TextView = itemView.findViewById(R.id.userName)  // Added userName here
        val messageTime: TextView = itemView.findViewById(R.id.messageTime)
        val senderImage: ImageView = itemView.findViewById(R.id.senderImage)
    }
}
