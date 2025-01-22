package com.example.myapplication.model

class RecipeModel private constructor() {
    val recipes: MutableList<Recipe> = ArrayList()

    companion object {
        val shared = RecipeModel()
    }

    init {
        for (i in 0..20) {
            val recipe = Recipe(
                name = "Recipe $i",
                description = "This is a description for Recipe $i",
                rating = (3.0 + i % 3).toFloat(),
                imageUrl = "" // You can replace this with actual URLs if needed
            )
            recipes.add(recipe)
        }
    }
}

data class Recipe(
    val name: String,
    val description: String,
    val rating: Float,
    val imageUrl: String
)
