package com.pratik.iiits.notes.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pratik.iiits.Models.Poll
import com.pratik.iiits.databinding.ItemPollBinding

class PollAdapter(private val context: Context, private val polls: List<Poll>) : RecyclerView.Adapter<PollAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemPollBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount() = polls.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(polls[position])
    }

    inner class ViewHolder(private val binding: ItemPollBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(poll: Poll) {
            binding.tvPollQuestion.text= poll.question
            binding.llPollOptions.removeAllViews()
            poll.options.forEach { option ->
                val optionTextView = TextView(context)
                optionTextView.text = option
                binding.llPollOptions.addView(optionTextView)
            }
        }
    }
}