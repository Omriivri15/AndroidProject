package com.example.myapplication.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.LoginActivity
import com.example.myapplication.R
import com.example.myapplication.adapter.RecipeAdapter
import com.example.myapplication.model.Recipe
import com.example.myapplication.model.RecipeModel
import com.google.firebase.auth.FirebaseAuth

class RecipesListFragment : Fragment() {

    private var recipes: MutableList<Recipe>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_recipes_list, container, false)

        // Fetch recipes from the model
        recipes = RecipeModel.shared.recipes

        // Set up RecyclerView
        val recyclerView: RecyclerView = view.findViewById(R.id.recipes_list_activity_recycler_view)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(context)

        // Set up the adapter
        val adapter = RecipeAdapter(recipes)
        recyclerView.adapter = adapter

        // Set up Toolbar
        val toolbar: Toolbar = view.findViewById(R.id.toolbar_recipes_list)
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)
        (activity as AppCompatActivity).supportActionBar?.title = "ReciAppes"

        // Add profile icon
        val profileIcon: ImageView = view.findViewById(R.id.profile_icon)
        profileIcon.setOnClickListener {
            showProfileMenu(it)
        }

        return view
    }

    // Show Popup Menu for Profile
    private fun showProfileMenu(anchor: View) {
        val popupMenu = PopupMenu(requireContext(), anchor)
        popupMenu.menuInflater.inflate(R.menu.menu_profile, popupMenu.menu)

        // Handle menu item clicks
        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_profile -> {
                    // Navigate to Profile
                    navigateToProfile()
                    true
                }
                R.id.action_logout -> {
                    // Perform Logout
                    logoutUser()
                    true
                }
                else -> false
            }
        }
        popupMenu.show()
    }

    private fun navigateToProfile() {

    }

    private fun logoutUser() {
        // בצע לוגאוט דרך Firebase
        FirebaseAuth.getInstance().signOut()

        val intent = Intent(requireContext(), LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK // מנקה את ה-back stack
        startActivity(intent)
        requireActivity().finish()
    }
}
