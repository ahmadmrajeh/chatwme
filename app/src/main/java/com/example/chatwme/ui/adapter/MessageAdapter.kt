package com.example.chatwme.ui.adapter
 import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.*
 import com.example.chatwme.R
 import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
 import com.example.chatwme.databinding.ImageMessageBinding
import com.example.chatwme.databinding.MessageBinding
import com.example.chatwme.model.MessageBody


class MessageAdapter(
    private val options: FirebaseRecyclerOptions<MessageBody>,
    private val currentUserName: String?
) : FirebaseRecyclerAdapter<MessageBody,  ViewHolder>(options) {


     override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return if (viewType == VIEW_TYPE_TEXT) {
            val view = inflater.inflate(R.layout.message, parent, false)
            val binding = MessageBinding.bind(view)
            MessageViewHolder(binding,currentUserName)
        } else  {

            val view = inflater.inflate(R.layout.image_message, parent, false)
            val binding = ImageMessageBinding.bind(view)

            ImageMessageViewHolder(binding)
         }
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int, model: MessageBody) {
        Log.d("tess", "getItemViewType: $position , ${options.snapshots.size} ")
        if (holder is MessageViewHolder) {
            holder.bind(model)
        } else if (holder is ImageMessageViewHolder) {
            holder.bind(model)
         }
    }

    override fun getItemViewType(position: Int): Int {
        return if (options.snapshots[position].text != null) VIEW_TYPE_TEXT else VIEW_TYPE_IMAGE
    }

    companion object {
        const val TAG = "MessageAdapter"
        const val VIEW_TYPE_TEXT = 1
        const val VIEW_TYPE_IMAGE = 2
    }

}