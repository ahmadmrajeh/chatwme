package com.example.chatwme.ui.adapter.chatAdapter

import androidx.recyclerview.widget.RecyclerView
import com.example.chatwme.R
import com.example.chatwme.databinding.SenderBinding
import com.example.chatwme.model.MessageBody
import com.example.chatwme.ui.MainActivity
import com.example.chatwme.ui.adapter.chatAdapter.ImageMessageViewHolder.Companion.loadImageIntoView

class SenderViewHolder(private val binding: SenderBinding, private val currentUserName: String?) :
    RecyclerView.ViewHolder(binding.root) {


    fun bind(item: MessageBody) {
        binding.message.text = item.text
        MessageViewHolder.setTextColor(item.name, binding.message, currentUserName)

        binding.username.text = item.name ?: MainActivity.ANONYMOUS
        if (item.photoUrl != null) {
            loadImageIntoView(binding.messengerImageView, item.photoUrl)
        } else {
            binding.messengerImageView.setImageResource(R.drawable.ic_account_circle_black_36dp)
        }

    }


}