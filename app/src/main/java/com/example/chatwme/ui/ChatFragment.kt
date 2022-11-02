package com.example.chatwme.ui

import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chatwme.databinding.FragmentChatBinding
import com.example.chatwme.model.MessageBody
import com.example.chatwme.ui.adapter.chatAdapter.MessageAdapter
import com.example.chatwme.utils.ButtonObserver
import com.example.chatwme.utils.OpenDocumentContract
import com.example.chatwme.utils.ScrollToBottomObserver
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.android.material.transition.MaterialContainerTransform
import com.google.android.material.transition.MaterialFadeThrough
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage


class ChatFragment : Fragment() {
    private lateinit var mBundleRecyclerViewState: Bundle
    lateinit var binding: FragmentChatBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var manager: LinearLayoutManager
    private lateinit var db: FirebaseDatabase
    private lateinit var recyclerViewState: Parcelable
    private lateinit var adapter: MessageAdapter
    private val openDocument = registerForActivityResult(OpenDocumentContract()) { uri ->
        uri?.let {
            onImageSelected(it)
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChatBinding.inflate(inflater)

        auth = Firebase.auth
        setUpScreen()
        exitTransition = MaterialContainerTransform()
        enterTransition = MaterialFadeThrough()
        return binding.root
    }


    private fun setUpScreen() {
        db = Firebase.database
        val messagesRef = db.reference.child(MESSAGES_CHILD)
        val options = FirebaseRecyclerOptions.Builder<MessageBody>()
            .setQuery(messagesRef, MessageBody::class.java)
            .build()
        setUpAdapters(options)

        clickListeners()
    }


    private fun clickListeners() {
        binding.messageEditText.addTextChangedListener(ButtonObserver(binding.sendButton))

        binding.sendButton.setOnClickListener {

            textMessage(binding.messageEditText.text.toString())

            binding.messageEditText.setText("")
        }

        binding.addMessageImageView.setOnClickListener {
            openDocument.launch(arrayOf("image/*"))
        }


    }

    private fun setUpAdapters(options: FirebaseRecyclerOptions<MessageBody>) {
        adapter = MessageAdapter(options, getUserName())
        adapter.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.ALLOW
        binding.progressBar.visibility = ProgressBar.INVISIBLE


        manager = LinearLayoutManager(requireActivity()) //CustomLinearLayoutManager(this)
        manager.stackFromEnd = true
        binding.messageRecyclerView.layoutManager = manager
        binding.messageRecyclerView.adapter = adapter


        adapter.registerAdapterDataObserver(
            ScrollToBottomObserver(
                binding.messageRecyclerView,
                adapter, manager
            )
        )
        adapter.startListening()

    }


    private fun getPhotoUrl(): String? {
        val user = auth.currentUser
        return user?.photoUrl?.toString()
    }

    private fun getUserName(): String? {
        val user = auth.currentUser
        return if (user != null) {
            user.displayName
        } else ANONYMOUS
    }


    private fun onImageSelected(uri: Uri) {
        Log.d(TAG, "Uri: $uri")
        val user = auth.currentUser
        val tempMessage = MessageBody(null, getUserName(), getPhotoUrl(), LOADING_IMAGE_URL)
        db.reference
            .child(MESSAGES_CHILD)
            .push()
            .setValue(
                tempMessage,
                DatabaseReference.CompletionListener { databaseError, databaseReference ->
                    if (databaseError != null) {
                        Log.w(
                            TAG, "Unable to write message to database.",
                            databaseError.toException()
                        )
                        return@CompletionListener
                    }

                    val key = databaseReference.key
                    val storageReference = Firebase.storage
                        .getReference(user!!.uid)
                        .child(key!!)
                        .child(uri.lastPathSegment!!)
                    putImageInStorage(storageReference, uri, key)
                })
    }


    private fun textMessage(text: String) {
        val bodyMessage = MessageBody(
            text,
            getUserName(),
            getPhotoUrl(), null
        )

        //  userId = FirebaseAuth.getInstance().currentUser?.uid!!
        db.reference.child(MESSAGES_CHILD)
            .push().setValue(bodyMessage)
    }


    private fun putImageInStorage(storageReference: StorageReference, uri: Uri, key: String?) {

        storageReference.putFile(uri)
            .addOnSuccessListener(
                requireActivity()
            ) { taskSnapshot ->

                taskSnapshot.metadata!!.reference!!.downloadUrl
                    .addOnSuccessListener { uri ->
                        val messageBody =
                            MessageBody(null, getUserName(), getPhotoUrl(), uri.toString())

                        db.reference
                            .child(MainActivity.MESSAGES_CHILD)
                            .child(key!!)
                            .setValue(messageBody)
                        Log.e(
                            "upload",
                            "Image upload task was  successful."
                        )
                    }
            }
            .addOnFailureListener(requireActivity()) { e ->
                Log.w(
                    "failedupload",
                    "Image upload task was unsuccessful.",
                    e
                )
            }
    }


    override fun onPause() {
        super.onPause()

        mBundleRecyclerViewState = Bundle()
        val listState: Parcelable? =
            binding.messageRecyclerView.layoutManager?.onSaveInstanceState()
        mBundleRecyclerViewState.putParcelable("KEY_RECYCLER_STATE", listState)


        adapter.stopListening()

    }


    override fun onResume() {
        super.onResume()
        if (this::mBundleRecyclerViewState.isInitialized) {
            val listState = mBundleRecyclerViewState.getParcelable<Parcelable>("KEY_RECYCLER_STATE")
            binding.messageRecyclerView.layoutManager?.onRestoreInstanceState(listState)
        }


    }


    companion object {
        private const val TAG = "MainAct2"
        const val MESSAGES_CHILD = "messages"
        const val ANONYMOUS = "anonymous"
        const val LOADING_IMAGE_URL = "https://www.google.com/images/spin-32.gif"
    }

}