package com.example.myapplication.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.adapter.RecipeAdapter
import com.example.myapplication.model.Recipe
import com.example.myapplication.model.RecipeModel

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

        // Add profile icon as a separate ImageView
        val profileIcon: ImageView = view.findViewById(R.id.profile_icon)
        profileIcon.setOnClickListener {
            // Open profile activity or fragment
        }

        return view
    }
}