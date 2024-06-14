package com.pratik.iiits.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.pratik.iiits.R

class MessagesAdapter(private val messagesList: List<com.pratik.iiits.Models.Message>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val MESSAGE_TYPE_TEXT = 1
    private val MESSAGE_TYPE_IMAGE = 2

    inner class TextMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val messageText: TextView = itemView.findViewById(R.id.message_text)
    }

    inner class ImageMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val messageImage: ImageView = itemView.findViewById(R.id.message_image)
    }

    override fun getItemViewType(position: Int): Int {
        return if (messagesList[position].imageUrl != null) {
            MESSAGE_TYPE_IMAGE
        } else {
            MESSAGE_TYPE_TEXT
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == MESSAGE_TYPE_TEXT) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_text_message, parent, false)
            TextMessageViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_image_message, parent, false)
            ImageMessageViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messagesList[position]
        if (holder is TextMessageViewHolder) {
            holder.messageText.text = message.message
        } else if (holder is ImageMessageViewHolder) {
            Glide.with(holder.messageImage.context)
                .load(message.imageUrl)
                .into(holder.messageImage)
        }
    }

    override fun getItemCount(): Int {
        return messagesList.size
    }
}
