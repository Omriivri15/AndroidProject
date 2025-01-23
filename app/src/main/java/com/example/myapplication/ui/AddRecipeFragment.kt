package com.example.myapplication.ui

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.model.Recipe
import com.example.myapplication.model.RecipeModel

class AddRecipeFragment : Fragment() {

    private lateinit var recipeTitleInput: EditText
    private lateinit var ingredientsRecyclerView: RecyclerView
    private lateinit var stepsRecyclerView: RecyclerView
    private lateinit var uploadPhotoButton: Button
    private lateinit var saveRecipeButton: Button
    private lateinit var ingredientsAdapter: IngredientsAdapter
    private lateinit var stepsAdapter: StepsAdapter
    private var selectedPhotoUri: Uri? = null

    private val imagePickerLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            selectedPhotoUri = it
            Toast.makeText(requireContext(), "Photo selected", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_add_recipe, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar(view)
        initializeViews(view)
        setupAdapters()
        setupListeners()
    }

    private fun setupToolbar(view: View) {
        val toolbar: Toolbar = view.findViewById(R.id.toolbar)
        (activity as AppCompatActivity).apply {
            setSupportActionBar(toolbar)
            supportActionBar?.apply {
                title = "Add Your Recipe"
                setDisplayHomeAsUpEnabled(true)
            }
        }
        toolbar.setNavigationOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    private fun initializeViews(view: View) {
        recipeTitleInput = view.findViewById(R.id.recipe_title_input)
        ingredientsRecyclerView = view.findViewById(R.id.ingredients_recycler_view)
        stepsRecyclerView = view.findViewById(R.id.steps_recycler_view)
        uploadPhotoButton = view.findViewById(R.id.upload_photo_button)
        saveRecipeButton = view.findViewById(R.id.save_recipe_button)
    }

    private fun setupAdapters() {
        ingredientsAdapter = IngredientsAdapter()
        stepsAdapter = StepsAdapter(mutableListOf(""))

        ingredientsRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = ingredientsAdapter
        }

        stepsRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = stepsAdapter
        }
    }

    private fun setupListeners() {
        uploadPhotoButton.setOnClickListener {
            imagePickerLauncher.launch("image/*")
        }

        saveRecipeButton.setOnClickListener {
            val title = recipeTitleInput.text.toString()
            val ingredients = ingredientsAdapter.getItems()
//            val steps = stepsAdapter.getItems()

            if (title.isBlank()) {
                Toast.makeText(requireContext(), "Please enter a recipe title", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

//            val newRecipe = Recipe(
//                title = title,
//                ingredients = ingredients,
//                steps = steps,
//                photoUri = selectedPhotoUri
//            )

//            RecipeModel.shared.addRecipe(newRecipe)
            Toast.makeText(requireContext(), "Recipe saved successfully", Toast.LENGTH_SHORT).show()
            requireActivity().supportFragmentManager.popBackStack()
        }
    }
}