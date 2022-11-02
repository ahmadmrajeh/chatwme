package com.example.chatwme.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chatwme.R
import com.example.chatwme.databinding.FragmentPostBinding
import com.example.chatwme.model.Record
import com.example.chatwme.ui.adapter.PostAdapter
import com.google.android.material.transition.MaterialContainerTransform
import com.google.android.material.transition.MaterialFadeThrough
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings

class PostFragment : Fragment() , PostAdapter.PostsAdapterListener {
private lateinit var binding :FragmentPostBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var adapter: PostAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding=   FragmentPostBinding.inflate(inflater)

        auth = Firebase.auth
        enterTransition = MaterialFadeThrough()
        exitTransition = MaterialContainerTransform()
        setUpScreen()
   
        return binding.root
    }
    private fun setUpScreen() {
        val db = FirebaseFirestore.getInstance().collection("records")
        setUpRV(db)
        remoteConfigDefined()

        clickListeners(db)


    }
    private fun setUpRV(db: Query) {
        val query: Query = db
        adapter = PostAdapter(query.orderBy("time"), this)
        binding.recyclerView.layoutManager = LinearLayoutManager(requireActivity())
        binding.recyclerView.adapter = adapter
        adapter.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
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

      
        binding.button4.setOnClickListener {
            throw RuntimeException("Test Crash")
        }

    }
    override fun onPostSelected(status: Record?) {

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
    
    private fun remoteConfigDefined() {
        val remoteConfig: FirebaseRemoteConfig = Firebase.remoteConfig
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 1
        }
        remoteConfig.setConfigSettingsAsync(configSettings)
        remoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)

        remoteConfig.fetchAndActivate()
            .addOnCompleteListener(requireActivity()) { task ->
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