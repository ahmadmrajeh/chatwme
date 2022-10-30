package com.example.chatwme.ui.adapter

import android.graphics.Color
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.chatwme.ui.MainActivity
import com.example.chatwme.R
import com.example.chatwme.ui.adapter.ImageMessageViewHolder.Companion.loadImageIntoView
import com.example.chatwme.databinding.MessageBinding
import com.example.chatwme.databinding.SenderBinding
import com.example.chatwme.model.MessageBody

class MessageViewHolder(private val binding: MessageBinding, private val currentUserName: String?)
    : RecyclerView.ViewHolder(binding.root) {




     fun bind(item: MessageBody) {
        binding.message .text = item.text
        setTextColor(item.name, binding.message,currentUserName)

        binding.username .text = item.name ?: MainActivity.ANONYMOUS
        if (item.photoUrl != null) {
            loadImageIntoView(binding.messengerImageView, item.photoUrl)
        } else {
            binding.messengerImageView.setImageResource(R.drawable.ic_account_circle_black_36dp)
        }

    }
  companion object {
      fun setTextColor(userName: String?, textView: TextView, currentUserName: String?) {
        if (userName != MainActivity.ANONYMOUS && currentUserName == userName && userName != null) {
            textView.setBackgroundResource(R.drawable.rounded_message_blue)
            textView.setTextColor(Color.WHITE)
        } else {
            textView.setBackgroundResource(R.drawable.rounded_message_gray)
            textView.setTextColor(Color.BLACK)
        }
    }}
}
class SenderViewHolder(private val binding: SenderBinding, private val currentUserName: String?)
    : RecyclerView.ViewHolder(binding.root) {


    fun bind(item: MessageBody) {
        binding.message .text = item.text
        MessageViewHolder. setTextColor(item.name, binding.message,currentUserName)

        binding.username .text = item.name ?: MainActivity.ANONYMOUS
        if (item.photoUrl != null) {
            loadImageIntoView(binding.messengerImageView, item.photoUrl)
        } else {
            binding.messengerImageView.setImageResource(R.drawable.ic_account_circle_black_36dp)
        }

    }


}
