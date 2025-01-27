package com.example.myapplication.ui;

import StepsAdapter;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.R;
import com.example.myapplication.model.Recipe;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import java.util.UUID;

class AddRecipeFragment : Fragment() {

    private lateinit var recipeTitleInput: EditText;
    private lateinit var ingredientsRecyclerView: RecyclerView;
    private lateinit var stepsRecyclerView: RecyclerView;
    private lateinit var uploadPhotoButton: Button;
    private lateinit var saveRecipeButton: Button;
    private lateinit var ingredientsAdapter: IngredientsAdapter;
    private lateinit var stepsAdapter: StepsAdapter;
    private var selectedPhotoUri: Uri? = null;

    private val imagePickerLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            selectedPhotoUri = it;
            Toast.makeText(requireContext(), "Photo selected", Toast.LENGTH_SHORT).show();
        }
    };

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_add_recipe, container, false);

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState);

        setupToolbar(view);
        initializeViews(view);
        setupAdapters();
        setupListeners();
    }

    private fun setupToolbar(view: View) {
        val toolbar: Toolbar = view.findViewById(R.id.toolbar);
        (activity as AppCompatActivity).apply {
            setSupportActionBar(toolbar);
            supportActionBar?.apply {
                title = "Add Your Recipe";
                setDisplayHomeAsUpEnabled(true);
            }
        }
        toolbar.setNavigationOnClickListener {
            requireActivity().supportFragmentManager.popBackStack();
        };
    }

    private fun initializeViews(view: View) {
        recipeTitleInput = view.findViewById(R.id.recipe_title_input);
        ingredientsRecyclerView = view.findViewById(R.id.ingredients_recycler_view);
        stepsRecyclerView = view.findViewById(R.id.steps_recycler_view);
        uploadPhotoButton = view.findViewById(R.id.upload_photo_button);
        saveRecipeButton = view.findViewById(R.id.save_recipe_button);
    }

    private fun setupAdapters() {
        ingredientsAdapter = IngredientsAdapter();
        stepsAdapter = StepsAdapter(mutableListOf(""));

        ingredientsRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext());
            adapter = ingredientsAdapter;
        }

        stepsRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext());
            adapter = stepsAdapter;
        }
    }

    private fun setupListeners() {
        uploadPhotoButton.setOnClickListener {
            imagePickerLauncher.launch("image/*");
        };

        saveRecipeButton.setOnClickListener {
            val title = recipeTitleInput.text.toString();
            val ingredients = ingredientsAdapter.getItems();
            val steps = stepsAdapter.getItems().filter { it.isNotBlank() };

            if (title.isBlank()) {
                Toast.makeText(requireContext(), "Please enter a recipe title", Toast.LENGTH_SHORT).show();
                return@setOnClickListener;
            }

            val description = steps.joinToString(separator = "\n");

            if (selectedPhotoUri != null) {
                uploadImageAndSaveRecipe(title, description, ingredients);
            } else {
                saveRecipeToFirestore(title, description, "");
            }
        };
    }

    private fun uploadImageAndSaveRecipe(title: String, description: String, ingredients: List<String>) {
        try {
            val storageReference = FirebaseStorage.getInstance().reference
                .child("recipes_images/${UUID.randomUUID()}.jpg")
            val inputStream = requireContext().contentResolver.openInputStream(selectedPhotoUri!!)

            if (inputStream == null) {
                Log.e("AddRecipeFragment", "InputStream is null for URI: $selectedPhotoUri")
                Toast.makeText(requireContext(), "Failed to resolve image URI", Toast.LENGTH_SHORT).show()
                return
            } else {
                Log.d("AddRecipeFragment", "InputStream opened successfully for URI: $selectedPhotoUri")
            }

            val uploadTask = storageReference.putStream(inputStream)
            uploadTask.addOnSuccessListener {
                storageReference.downloadUrl.addOnSuccessListener { uri ->
                    val imageUrl = uri.toString()
                    saveRecipeToFirestore(title, description, imageUrl)
                }.addOnFailureListener { e ->
                    Log.e("AddRecipeFragment", "Failed to get download URL: ${e.message}")
                    Toast.makeText(requireContext(), "Failed to get image URL", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener { e ->
                Log.e("AddRecipeFragment", "Image upload failed: ${e.message}")
                Log.d("AddRecipeFragment", "Selected photo URI: $selectedPhotoUri")
                Toast.makeText(requireContext(), "Image upload failed", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Log.e("AddRecipeFragment", "Error opening InputStream: ${e.message}")
            Toast.makeText(requireContext(), "Failed to upload image", Toast.LENGTH_SHORT).show()
        }
    }


    private fun saveRecipeToFirestore(title: String, description: String, imageUrl: String) {
        val newRecipe = Recipe(
            name = title,
            description = description,
            rating = 4.0f,
            imageUrl = imageUrl
        );

        val db = FirebaseFirestore.getInstance();
        db.collection("recipes")
            .add(newRecipe)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Recipe saved successfully", Toast.LENGTH_SHORT).show();
                requireActivity().supportFragmentManager.popBackStack();
            }
            .addOnFailureListener { e ->
                Log.e("AddRecipeFragment", "Error saving recipe: ${e.message}");
                Toast.makeText(requireContext(), "Error saving recipe", Toast.LENGTH_SHORT).show();
            };
    }
}
