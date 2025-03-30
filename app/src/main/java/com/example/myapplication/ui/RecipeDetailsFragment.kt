package com.example.myapplication.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.myapplication.R
import com.example.myapplication.model.Recipe

class RecipeDetailsFragment : Fragment() {

    private var recipe: Recipe? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        recipe = arguments?.getParcelable("recipe")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_recipe_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val nameTextView = view.findViewById<TextView>(R.id.detail_recipe_name)
        val descTextView = view.findViewById<TextView>(R.id.detail_recipe_description)
        val ingredientsTextView = view.findViewById<TextView>(R.id.detail_recipe_ingredients)
        val imageView = view.findViewById<ImageView>(R.id.detail_recipe_image)

        recipe?.let {
            nameTextView.text = it.name
            descTextView.text = it.description
            ingredientsTextView.text = it.ingredients?.joinToString("\n") ?: "No ingredients"
            Glide.with(requireContext()).load(it.imageUrl).into(imageView)
        }
    }

    companion object {
        fun newInstance(recipe: Recipe): RecipeDetailsFragment {
            val fragment = RecipeDetailsFragment()
            val bundle = Bundle()
            bundle.putParcelable("recipe", recipe)
            fragment.arguments = bundle
            return fragment
        }
    }
}
