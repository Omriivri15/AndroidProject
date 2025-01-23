package com.example.myapplication.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R

class IngredientsAdapter : RecyclerView.Adapter<IngredientsAdapter.IngredientViewHolder>() {

    private val ingredients = mutableListOf("")

    inner class IngredientViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val input: EditText = view.findViewById(R.id.ingredient_input)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IngredientViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_ingredient, parent, false)
        return IngredientViewHolder(view)
    }

    override fun onBindViewHolder(holder: IngredientViewHolder, position: Int) {
        holder.input.setText(ingredients[position])
        holder.input.doAfterTextChanged { text ->
            if (position == ingredients.size - 1 && text?.isNotEmpty() == true) {
                ingredients.add("")
                notifyItemInserted(ingredients.size - 1)
            }
        }
    }

    override fun getItemCount(): Int = ingredients.size

    fun getItems(): List<String> = ingredients.filter { it.isNotEmpty() }
}
