package com.example.myapplication.ui

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.myapplication.R
import com.example.myapplication.model.Recipe
import com.google.firebase.firestore.FirebaseFirestore

class EditRecipeDialogFragment(
    private val recipe: Recipe,
    private val onRecipeUpdated: (Recipe) -> Unit
) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_edit_recipe, null)

        val titleInput = view.findViewById<EditText>(R.id.edit_title_input)
        val ingredientsInput = view.findViewById<EditText>(R.id.edit_ingredients_input)
        val descriptionInput = view.findViewById<EditText>(R.id.edit_description_input)

        titleInput.setText(recipe.name)
        ingredientsInput.setText(recipe.ingredients?.joinToString("\n") ?: "")
        descriptionInput.setText(recipe.description)

        return AlertDialog.Builder(requireContext())
            .setTitle("Edit Recipe")
            .setView(view)
            .setPositiveButton("Save") { _, _ ->
                val updatedTitle = titleInput.text.toString().trim()
                val updatedIngredients = ingredientsInput.text.toString().trim()
                    .split("\n").filter { it.isNotBlank() }
                val updatedDescription = descriptionInput.text.toString().trim()

                val updates = mapOf(
                    "name" to updatedTitle,
                    "ingredients" to updatedIngredients,
                    "description" to updatedDescription
                )

                FirebaseFirestore.getInstance().collection("recipes")
                    .document(recipe.id)
                    .update(updates)
                    .addOnSuccessListener {
                        val updatedRecipe = recipe.copy(
                            name = updatedTitle,
                            ingredients = updatedIngredients,
                            description = updatedDescription
                        )
                        onRecipeUpdated(updatedRecipe)

                        context?.let {
                            Toast.makeText(it, "Recipe updated!", Toast.LENGTH_SHORT).show()
                        }
                    }
                    .addOnFailureListener {
                        context?.let {
                            Toast.makeText(it, "Update failed", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
            .setNegativeButton("Cancel", null)
            .create()
    }
}
