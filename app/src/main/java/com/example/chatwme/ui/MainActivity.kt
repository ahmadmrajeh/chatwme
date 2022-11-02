package com.example.chatwme.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity

import com.example.chatwme.R
import com.example.chatwme.databinding.ActivityMainBinding
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth

import com.google.firebase.ktx.Firebase

import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI.setupWithNavController
import com.google.android.material.transition.MaterialFadeThrough

class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    private val signIn: ActivityResultLauncher<Intent> =
        registerForActivityResult(FirebaseAuthUIActivityResultContract(), this::onSignInResult)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        navController = Navigation.findNavController(this, R.id.fragmentContainerView)
        setupWithNavController(binding.bottomNavigation, navController)

        auth = Firebase.auth
        if (auth.currentUser == null) {

            forceLongedIn()

            // finish()
            // return
        }


    }

    private fun forceLongedIn(loggedOut: Boolean = false) {
        if (Firebase.auth.currentUser == null || loggedOut) {

            val signInIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setLogo(R.mipmap.ic_launcher)
                .setAvailableProviders(
                    listOf(
                        AuthUI.IdpConfig.EmailBuilder().build(),
                        AuthUI.IdpConfig.GoogleBuilder().build(),
                    )
                )
                .build()

            signIn.launch(signInIntent)
        }
    }


    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        if (result.resultCode == RESULT_OK) {
            Log.d(TAG, "Sign in successful!")
            // goToMainActivity()

        } else {

            val response = result.idpResponse
            if (response == null) {
                Log.w(TAG, "Sign in canceled")
            } else {
                Log.w(TAG, "Sign in error", response.error)
            }
        }
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
            R.id.sign -> {
                forceLongedIn()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }


    private fun signOut() {
        AuthUI.getInstance().signOut(this)


    }


    companion object {
        private const val TAG = "MainAct2"
        const val MESSAGES_CHILD = "messages"
        const val ANONYMOUS = "anonymous"
        const val LOADING_IMAGE_URL = "https://www.google.com/images/spin-32.gif"
    }


}
