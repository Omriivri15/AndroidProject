package com.example.myapplication.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myapplication.model.Recipe
import com.example.myapplication.R

class RecipeAdapter(private val recipes: MutableList<Recipe>?) : RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder>() {

    // ViewHolder class for Recipe
    class RecipeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.recipe_name_text_view)
        private val descriptionTextView: TextView = itemView.findViewById(R.id.recipe_description_text_view)
        private val ratingBar: RatingBar = itemView.findViewById(R.id.recipe_rating_bar)
        private val recipeImageView: ImageView = itemView.findViewById(R.id.recipe_image_view) // Add ImageView

        fun bind(recipe: Recipe?) {
            nameTextView.text = recipe?.name
            descriptionTextView.text = recipe?.description
            ratingBar.rating = recipe?.rating ?: 0.0f

            // Load the image using Glide
            Glide.with(itemView.context)
                .load(recipe?.imageUrl)  // URL of the image
                .into(recipeImageView)    // Set the image to the ImageView
        }
    }

    // Create the ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recipe_list_row, parent, false)
        return RecipeViewHolder(view)
    }

    // Bind the data to the ViewHolder
    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        val recipe = recipes?.get(position)
        holder.bind(recipe)
    }

    // Return the total count of items
    override fun getItemCount(): Int = recipes?.size ?: 0
}
