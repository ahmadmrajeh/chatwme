package com.example.chatwme.ui.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.chatwme.R
import com.example.chatwme.databinding.PostBinding
import com.example.chatwme.model.MessageBody
import com.google.firebase.firestore.Query


class PostAdapter(
    query: Query,
    private val listener: PostsAdapterListener
) : FirestoreAdapter<PostsViewHolder>(query) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):PostsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.post, parent, false)


        val binding = PostBinding.bind(view)
    return    PostsViewHolder(binding )
       }

    override fun onBindViewHolder(holder: PostsViewHolder, position: Int) {

        getSnapshot(position)?.let { snapshot ->

            holder.bind(snapshot, listener)

        }
    }


    interface PostsAdapterListener {
        fun onPostSelected(status: MessageBody?)
    }
}