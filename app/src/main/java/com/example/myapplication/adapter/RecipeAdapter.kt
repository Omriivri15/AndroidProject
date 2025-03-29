package com.example.myapplication.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myapplication.model.Recipe
import com.example.myapplication.R
import com.example.myapplication.ui.EditRecipeDialogFragment
//import com.example.myapplication.ui.EditRecipeFragment
import com.google.firebase.firestore.FirebaseFirestore

class RecipeAdapter(private val recipes: MutableList<Recipe>, private val currentUserId: String) : RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder>() {

    class RecipeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.recipe_name_text_view)
        private val descriptionTextView: TextView = itemView.findViewById(R.id.recipe_description_text_view)
        private val ratingBar: RatingBar = itemView.findViewById(R.id.recipe_rating_bar)
        private val recipeImageView: ImageView = itemView.findViewById(R.id.recipe_image_view)
        private val editButton: View = itemView.findViewById(R.id.edit_button)
        private val deleteButton: View = itemView.findViewById(R.id.delete_button)

        fun bind(recipe: Recipe, currentUserId: String, onDelete: (Recipe) -> Unit, onEdit: (Recipe, View) -> Unit) {
            nameTextView.text = recipe.name
            descriptionTextView.text = recipe.description
            ratingBar.rating = recipe.rating

            // Show Edit and Delete buttons only if the user is the owner of the recipe
            if (recipe.ownerId == currentUserId) {
                editButton.visibility = View.VISIBLE
                deleteButton.visibility = View.VISIBLE

                editButton.setOnClickListener { onEdit(recipe, itemView) }
                deleteButton.setOnClickListener { onDelete(recipe) }
            } else {
                editButton.visibility = View.GONE
                deleteButton.visibility = View.GONE
            }

            Glide.with(itemView.context)
                .load(recipe.imageUrl)
                .into(recipeImageView)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recipe_list_row, parent, false)
        return RecipeViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        val recipe = recipes[position]
        holder.bind(recipe, currentUserId, ::onDeleteRecipe, ::onEditRecipe)
    }

    override fun getItemCount(): Int = recipes.size

    private fun onDeleteRecipe(recipe: Recipe) {
        val db = FirebaseFirestore.getInstance()
        db.collection("recipes").document(recipe.id)  // השתמש במזהה המסמך
            .delete()
            .addOnSuccessListener {
                // Handle successful deletion
                val position = recipes.indexOf(recipe)
                recipes.removeAt(position)
                notifyItemRemoved(position)  // עדכון RecyclerView
            }
    }

    private fun onEditRecipe(recipe: Recipe, itemView: View) {
        val fragmentManager = (itemView.context as? AppCompatActivity)?.supportFragmentManager
        fragmentManager?.let {
            val dialog = EditRecipeDialogFragment(recipe) { updatedRecipe ->
                val index = recipes.indexOfFirst { it.id == updatedRecipe.id }
                if (index != -1) {
                    recipes[index] = updatedRecipe
                    notifyItemChanged(index)
                }
            }
            dialog.show(it, "EditRecipeDialog")
        }
    }

}
