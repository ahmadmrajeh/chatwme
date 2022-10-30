package com.example.chatwme.ui.adapter

import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import com.example.chatwme.databinding.PostBinding
import com.example.chatwme.model.MessageBody
import com.example.chatwme.model.Record
import com.google.firebase.firestore.DocumentSnapshot

class PostsViewHolder(
    private val binding: PostBinding
) : RecyclerView.ViewHolder(binding.root) {


    fun bind(snapshot: DocumentSnapshot, listener: PostAdapter.PostsAdapterListener) {
        val status: Record? = snapshot.toObject(Record::class.java)
        Log.e("snap", "we are in bind")
        binding.messager.text= status?.name
        binding.messenge.text=status?.text


  binding.root .setOnClickListener {
            listener.onPostSelected(status)

    }
}
}