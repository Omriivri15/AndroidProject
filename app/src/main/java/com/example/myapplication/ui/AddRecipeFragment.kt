package com.example.myapplication.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R

class AddRecipeFragment : Fragment() {

    private lateinit var recipeTitleInput: EditText
    private lateinit var ingredientsRecyclerView: RecyclerView
    private lateinit var stepsRecyclerView: RecyclerView
    private lateinit var uploadPhotoButton: Button
    private lateinit var saveRecipeButton: Button
    private lateinit var ingredientsAdapter: IngredientsAdapter
    private lateinit var stepsAdapter: StepsAdapter
    private val steps = mutableListOf("")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add_recipe, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recipeTitleInput = view.findViewById(R.id.recipe_title_input)
        ingredientsRecyclerView = view.findViewById(R.id.ingredients_recycler_view)
        stepsRecyclerView = view.findViewById(R.id.steps_recycler_view)
        uploadPhotoButton = view.findViewById(R.id.upload_photo_button)
        saveRecipeButton = view.findViewById(R.id.save_recipe_button)

        ingredientsAdapter = IngredientsAdapter()
        stepsAdapter = StepsAdapter(steps)

        ingredientsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        ingredientsRecyclerView.adapter = ingredientsAdapter

        stepsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        stepsRecyclerView.adapter = stepsAdapter

        // העלאת תמונה
        uploadPhotoButton.setOnClickListener {
            // כאן תוכל להוסיף לוגיקה להעלאת תמונה
        }

        // שמירת המתכון
        saveRecipeButton.setOnClickListener {
            val title = recipeTitleInput.text.toString()
            val ingredients = ingredientsAdapter.getItems()


            // כאן תוכל להוסיף את הלוגיקה לשמירת המתכון
        }
    }
}
