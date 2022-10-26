package com.example.chatwme.adapter

import android.graphics.Color
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.chatwme.MainActivity
import com.example.chatwme.R
import com.example.chatwme.adapter.ImageMessageViewHolder.Companion.loadImageIntoView
import com.example.chatwme.databinding.MessageBinding
import com.example.chatwme.model.MessageBody

class MessageViewHolder(private val binding: MessageBinding, private val currentUserName: String?)
    : RecyclerView.ViewHolder(binding.root) {




     fun bind(item: MessageBody) {
        binding.messageTextView.text = item.text
        setTextColor(item.name, binding.messageTextView)

        binding.messengerTextView.text = item.name ?: MainActivity.ANONYMOUS
        if (item.photoUrl != null) {
            loadImageIntoView(binding.messengerImageView, item.photoUrl)
        } else {
            binding.messengerImageView.setImageResource(R.drawable.ic_account_circle_black_36dp)
        }

    }

    private fun setTextColor(userName: String?, textView: TextView) {
        if (userName != MainActivity.ANONYMOUS && currentUserName == userName && userName != null) {
            textView.setBackgroundResource(R.drawable.rounded_message_blue)
            textView.setTextColor(Color.WHITE)
        } else {
            textView.setBackgroundResource(R.drawable.rounded_message_gray)
            textView.setTextColor(Color.BLACK)
        }
    }
}
