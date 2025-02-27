package com.example.myapplication

import RecipesListFragment
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.myapplication.ui.AddRecipeFragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth
    private var inDisplayFragment: Fragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        firebaseAuth = FirebaseAuth.getInstance()

        // Check if the user is logged in; if not, redirect to LoginActivity
        if (firebaseAuth.currentUser == null) {
            navigateToLoginActivity()
            return
        }

        setContentView(R.layout.activity_main)

        // Display the recipe feed as the default fragment
        if (savedInstanceState == null) {
            displayFragment(RecipesListFragment())
        }


    }

    // Function to display a fragment
    fun displayFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.main_activity_frame_layout, fragment)
            addToBackStack(null)
            commit()
        }
        inDisplayFragment = fragment
    }

    // Navigate to LoginActivity if the user is not logged in
    private fun navigateToLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}