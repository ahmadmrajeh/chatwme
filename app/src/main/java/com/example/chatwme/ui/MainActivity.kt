package com.example.chatwme.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatwme.R
import com.example.chatwme.ui.adapter.MessageAdapter
import com.example.chatwme.databinding.ActivityMainBinding
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.example.chatwme.model.MessageBody
import com.example.chatwme.utils.ButtonObserver
import com.example.chatwme.utils.OpenDocumentContract
import com.example.chatwme.utils.ScrollToBottomObserver
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage


class MainActivity : AppCompatActivity() {
    private lateinit var userId: String
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityMainBinding
    private lateinit var manager: LinearLayoutManager
    private lateinit var db: FirebaseDatabase
    private lateinit var adapter: MessageAdapter
    private val openDocument =
        registerForActivityResult(OpenDocumentContract()) { uri ->
            uri?.let {
                onImageSelected(it)
            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth
        if (auth.currentUser == null) {
            startActivity(Intent(this, SignInActivity::class.java))
            finish()
            return }

        setUpScreen()

    }

    private fun setUpScreen() {
        db = Firebase.database
        val messagesRef = db.reference.child(MESSAGES_CHILD)
        val options = FirebaseRecyclerOptions.Builder<MessageBody>()
            .setQuery(messagesRef, MessageBody::class.java)
            .build()
        setUpAdapters(options)

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
        binding.progressBar.visibility = ProgressBar.INVISIBLE
        manager = LinearLayoutManager(this) //CustomLinearLayoutManager(this)
        manager.stackFromEnd = true
        binding.messageRecyclerView.layoutManager = manager
        binding.messageRecyclerView.adapter = adapter


        adapter.registerAdapterDataObserver(
            ScrollToBottomObserver(
                binding.messageRecyclerView,
                adapter, manager
            )
        )
    }

    public override fun onPause() {
        adapter.stopListening()
        super.onPause()
    }

    public override fun onResume() {
        super.onResume()
        adapter.startListening()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.sign_out_menu -> {
                signOut()
                true
            }
            R.id.posts_Activity -> {
startActivity(Intent(this, PostActivity::class.java))
                return true
            }

            else -> super.onOptionsItemSelected(item)
        }
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


    private fun textMessage(text : String) {
        val bodyMessage = MessageBody(
            text,
            getUserName(),
            getPhotoUrl(), null
        )

        //  userId = FirebaseAuth.getInstance().currentUser?.uid!!
        db.reference.child(MESSAGES_CHILD).push().setValue(bodyMessage)
    }

    private fun putImageInStorage(storageReference: StorageReference, uri: Uri, key: String?) {

        storageReference.putFile(uri)
            .addOnSuccessListener(
                this
            ) { taskSnapshot ->

                taskSnapshot.metadata!!.reference!!.downloadUrl
                    .addOnSuccessListener { uri ->
                        val messageBody =
                            MessageBody(null, getUserName(), getPhotoUrl(), uri.toString())

                        db.reference
                            .child(MESSAGES_CHILD)
                            .child(key!!)
                            .setValue(messageBody)
                        Log.e(
                            "upload",
                            "Image upload task was  successful."
                        )
                    }
            }
            .addOnFailureListener(this) { e ->
                Log.w(
                    "failedupload",
                    "Image upload task was unsuccessful.",
                    e
                )
            }
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

    private fun signOut() {
        AuthUI.getInstance().signOut(this)
        startActivity(Intent(this, SignInActivity::class.java))
        finish()
    }

    companion object {
        private const val TAG = "MainAct2"
        const val MESSAGES_CHILD = "messages"
        const val ANONYMOUS = "anonymous"
        const val LOADING_IMAGE_URL = "https://www.google.com/images/spin-32.gif"
    }

}
