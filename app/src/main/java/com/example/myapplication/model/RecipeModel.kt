package com.example.myapplication.model

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class RecipeModel private constructor() {
    val recipes: MutableList<Recipe> = ArrayList()
    private val db = FirebaseFirestore.getInstance()
    private var listenerRegistration: ListenerRegistration? = null

    companion object {
        val shared = RecipeModel()
    }

    init {
        fetchRecipesFromFirestore()
    }

    private fun fetchRecipesFromFirestore() {
        // Clear current recipes in case of multiple calls
        recipes.clear()

        // Listen for real-time updates from Firestore
        listenerRegistration = db.collection("recipes")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    println("Error fetching recipes: ${error.message}")
                    return@addSnapshotListener
                }

                // Clear the list to avoid duplicates
                recipes.clear()

                // Add recipes from Firestore
                snapshot?.documents?.forEach { document ->
                    val recipe = document.toObject(Recipe::class.java)
                    recipe?.let { recipes.add(it) }
                }
            }
    }

    fun stopListening() {
        // Stop listening for Firestore updates
        listenerRegistration?.remove()
    }
}

data class Recipe(
    val name: String = "",
    val description: String = "",
    val rating: Float = 0.0f,
    var imageUrl: String = ""
)
