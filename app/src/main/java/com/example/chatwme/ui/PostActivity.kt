package com.example.chatwme.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chatwme.R
import com.example.chatwme.databinding.ActivityPostBinding
import com.example.chatwme.model.Record
import com.example.chatwme.ui.adapter.PostAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings


class PostActivity : AppCompatActivity(), PostAdapter.PostsAdapterListener {

    private lateinit var auth: FirebaseAuth
    private lateinit var adapter: PostAdapter
    private lateinit var binding: ActivityPostBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth

        setUpScreen()

    }

    private fun setUpScreen() {
        val db = FirebaseFirestore.getInstance().collection("records")
        setUpRV(db)
        remoteConfigDefined()

        clickListeners(db)


    }

    private fun clickListeners(db: CollectionReference) {
        binding.button2.setOnClickListener {
            val status = binding.statusText.text

            if (status.isNotBlank() || status.isNotEmpty()) {


                addToFireStore(
                    db, Record(
                        status.toString(), getUserName(), getPhotoUrl(),
                        null, System.currentTimeMillis()
                    )
                )
                binding.statusText.text = null

            }

        }

        binding.button3.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
        binding.button4.setOnClickListener {
            throw RuntimeException("Test Crash")
        }

    }

    private fun remoteConfigDefined() {
        val remoteConfig: FirebaseRemoteConfig = Firebase.remoteConfig
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 1
        }
        remoteConfig.setConfigSettingsAsync(configSettings)
        remoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)

        remoteConfig.fetchAndActivate()
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    configPosting(remoteConfig.getBoolean("country"))
                }
            }
    }

    private fun configPosting(notAllowed: Boolean) {
        if (notAllowed) {

            binding.button2.visibility = View.INVISIBLE
            binding.statusText.hint =
                "posting is not allowed for you, please contact app developers"
        } else {

            binding.button2.visibility = View.VISIBLE
            binding.statusText.hint = "write something"
        }
    }

    private fun setUpRV(db: Query) {
        val query: Query = db
        adapter = PostAdapter(query.orderBy("time"), this)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
        adapter.stateRestorationPolicy = RecyclerView.Adapter
            .StateRestorationPolicy.PREVENT_WHEN_EMPTY
    }

    private fun addToFireStore(db: CollectionReference, data: Record) {

        db.add(data)
            .addOnSuccessListener { documentReference ->
                Log.e("TAGfire", "DocumentSnapshot written with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.e("TAGfire", "Error adding document" + e.message)
            }

    }

    override fun onPostSelected(status: Record?) {

    }

    override fun onStart() {
        super.onStart()
        adapter.startListening()
    }

    override fun onStop() {
        super.onStop()
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
        } else MainActivity.ANONYMOUS
    }

}