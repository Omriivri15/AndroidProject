package com.example.myapplication.adapter

import android.view.View
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.model.Recipe

class RecipeViewHolder(
    itemView: View,
    private val listener: OnItemClickListener?
) : RecyclerView.ViewHolder(itemView) {

    private val nameTextView: TextView = itemView.findViewById(R.id.recipe_name_text_view)
    private val descriptionTextView: TextView = itemView.findViewById(R.id.recipe_description_text_view)
    private val ratingBar: RatingBar = itemView.findViewById(R.id.recipe_rating_bar)
    private val imageView: ImageView = itemView.findViewById(R.id.recipe_image_view)

    init {
        itemView.setOnClickListener {
            listener?.onItemClick(adapterPosition)
        }
    }

    fun bind(recipe: Recipe) {
        nameTextView.text = recipe.name
        descriptionTextView.text = recipe.description
        ratingBar.rating = recipe.rating

        // Set a placeholder or load image from URL
        imageView.setImageResource(R.drawable.man)
    }
}

interface OnItemClickListener {
    fun onItemClick(position: Int)
}
