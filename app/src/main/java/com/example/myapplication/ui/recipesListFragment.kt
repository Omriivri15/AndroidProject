package com.example.myapplication.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.LoginActivity
import com.example.myapplication.R
import com.example.myapplication.adapter.RecipeAdapter
import com.example.myapplication.model.Recipe
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RecipesListFragment : Fragment() {

    private lateinit var adapter: RecipeAdapter
    private var recipesList: MutableList<Recipe> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_recipes_list, container, false)

        // Initialize the adapter with the current user ID
        adapter = RecipeAdapter(recipesList, FirebaseAuth.getInstance().currentUser?.uid ?: "")

        // RecyclerView setup
        val recyclerView: RecyclerView = view.findViewById(R.id.recipes_list_activity_recycler_view)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter

        // Toolbar setup
        val toolbar: androidx.appcompat.widget.Toolbar = view.findViewById(R.id.toolbar_recipes_list)
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        (activity as AppCompatActivity).supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(false)
            title = "ReciAppes"
        }

        // Profile icon
        val profileIcon: ImageView = view.findViewById(R.id.profile_icon)
        profileIcon.setOnClickListener {
            showProfileMenu(it)
        }

        // FAB to navigate to AddRecipeFragment
        val fab: FloatingActionButton = view.findViewById(R.id.fab_add_recipe)
        fab.setOnClickListener {
            findNavController().navigate(R.id.action_recipesListFragment_to_addRecipeFragment)
        }

        fetchRecipes()
        return view
    }

    override fun onStart() {
        super.onStart()
        adapter.notifyDataSetChanged()
    }

    private fun fetchRecipes() {
        val db = FirebaseFirestore.getInstance()
        db.collection("recipes").get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    recipesList.clear()
                    task.result?.let { documents ->
                        for (document in documents) {
                            val recipe = document.toObject(Recipe::class.java)
                            recipe.id = document.id
                            recipesList.add(recipe)
                        }
                        adapter.notifyDataSetChanged()
                    }
                }
            }
    }

    private fun showProfileMenu(anchor: View) {
        val popupMenu = PopupMenu(requireContext(), anchor)
        popupMenu.menuInflater.inflate(R.menu.menu_profile, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_profile -> {
                    // Navigate to ProfileFragment
                    findNavController().navigate(R.id.action_recipesListFragment_to_profileFragment)
                    true
                }
                R.id.action_logout -> {
                    logoutUser()
                    true
                }
                else -> false
            }
        }
        popupMenu.show()
    }

    private fun logoutUser() {
        FirebaseAuth.getInstance().signOut()
        val intent = Intent(requireContext(), LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        requireActivity().finish()
    }
}
