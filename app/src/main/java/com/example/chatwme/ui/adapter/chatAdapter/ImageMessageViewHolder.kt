package com.example.chatwme.ui.adapter.chatAdapter

import android.util.Log
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chatwme.ui.MainActivity
import com.example.chatwme.R
import com.example.chatwme.databinding.ImageMessageBinding
import com.example.chatwme.model.MessageBody
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class ImageMessageViewHolder(private val binding: ImageMessageBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(item: MessageBody) {

        loadImageIntoView(
            binding.messageImageView,
            item.imageUrl ?: "https://www.google.com/images/spin-32.gif"
        )

        binding.username.text = item.name ?: MainActivity.ANONYMOUS
        if (item.photoUrl != null) {
            loadImageIntoView(binding.messengerImageView, item.photoUrl)
        } else {
            binding.messengerImageView.setImageResource(R.drawable.ic_account_circle_black_36dp)
        }
    }

    companion object {
        fun loadImageIntoView(view: ImageView, url: String) {
            if (url.startsWith("gs://")) {
                val storageReference = Firebase.storage.getReferenceFromUrl(url)
                storageReference.downloadUrl
                    .addOnSuccessListener { uri ->
                        val downloadUrl = uri.toString()

                        Glide.with(view.context)
                            .load(downloadUrl)

                            .into(view)
                    }
                    .addOnFailureListener { e ->
                        Log.w(
                            "MessageAdapter.TAG",
                            "Getting download url was not successful.",
                            e
                        )
                    }
            } else {
                Glide.with(view.context).load(url)
                    .into(view)
            }
        }
    }
}
