package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
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

        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Adjust padding for edge-to-edge content
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Display the recipe feed as the default fragment
        if (savedInstanceState == null) {
            displayFragment(RecipesListFragment())
        }

        // Set up the FAB to navigate to AddRecipeFragment
        val fab: FloatingActionButton = findViewById(R.id.fab_add_recipe)
        fab.setOnClickListener {
            displayFragment(AddRecipeFragment())
        }
    }

    // Function to display a fragment
    private fun displayFragment(fragment: Fragment) {
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
