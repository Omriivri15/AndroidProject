package com.example.myapplication.ui

import StepsAdapter
import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.model.Recipe
import com.example.myapplication.data.remote.CloudinaryModel // הוספנו את המודול של Cloudinary
import com.google.firebase.firestore.FirebaseFirestore
import java.io.File
import java.util.UUID
import kotlinx.coroutines.*
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import android.location.Location



class AddRecipeFragment : Fragment() {

    private lateinit var recipeTitleInput: EditText
    private lateinit var ingredientsRecyclerView: RecyclerView
    private lateinit var stepsRecyclerView: RecyclerView
    private lateinit var saveRecipeButton: Button
    private lateinit var photoImageView: ImageView
    private lateinit var ingredientsAdapter: IngredientsAdapter
    private lateinit var stepsAdapter: StepsAdapter
    private var selectedPhotoUri: Uri? = null
    private var cameraPhotoUri: Uri? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var currentLocation: Location? = null


    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                selectedPhotoUri = it
                photoImageView.setImageURI(it)
                Toast.makeText(requireContext(), "Photo selected from gallery", Toast.LENGTH_SHORT)
                    .show()
            }
        }

    private val cameraLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                selectedPhotoUri = cameraPhotoUri
                photoImageView.setImageURI(cameraPhotoUri)
                Toast.makeText(requireContext(), "Photo captured", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Camera action canceled", Toast.LENGTH_SHORT)
                    .show()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_recipe, container, false)

        photoImageView = view.findViewById(R.id.photo_image_view)

        return view
    }

    @SuppressLint("MissingPermission")
    private fun getLastKnownLocation() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                101
            )
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                currentLocation = location
            }
        }

        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                101
            )
            return
        }

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar(view)
        initializeViews(view)
        setupAdapters()
        setupListeners()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        getLastKnownLocation()

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
        photoImageView.setOnClickListener {
            showPhotoOptionDialog()
        }

        saveRecipeButton.setOnClickListener {
            val title = recipeTitleInput.text.toString()
            val ingredients = ingredientsAdapter.getItems()
            val steps = stepsAdapter.getItems().filter { it.isNotBlank() }


            if (title.isBlank()) {
                Toast.makeText(requireContext(), "Please enter a recipe title", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            val description = steps.joinToString(separator = "\n")

            if (selectedPhotoUri != null) {
                uploadImageAndSaveRecipe(title, description, ingredients)
            } else {
                saveRecipeToFirestore(title, description, "")
            }
        }
    }

    private fun showPhotoOptionDialog() {
        val options = arrayOf("Choose from Gallery", "Take a Photo")
        AlertDialog.Builder(requireContext())
            .setTitle("Add Photo")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> galleryLauncher.launch("image/*")
                    1 -> checkAndRequestCameraPermission()
                }
            }
            .show()
    }

    private fun checkAndRequestCameraPermission() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_CODE
            )
        } else {
            capturePhotoFromCamera()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                capturePhotoFromCamera()
            } else {
                Toast.makeText(requireContext(), "Camera permission denied", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun capturePhotoFromCamera() {
        val photoFile = createImageFile()
        cameraPhotoUri = FileProvider.getUriForFile(
            requireContext(),
            "com.example.myapplication.fileprovider",
            photoFile
        )
        cameraLauncher.launch(cameraPhotoUri)
    }

    private fun createImageFile(): File {
        return File.createTempFile("photo_", ".jpg", requireContext().cacheDir)
    }

    private fun uploadImageAndSaveRecipe(title: String, description: String, ingredients: List<String>) {
        // הפעלת קורוטינה
        CoroutineScope(Dispatchers.Main).launch {
            try {
                // מעלה את התמונה ל-Cloudinary
                val bitmap = MediaStore.Images.Media.getBitmap(requireContext().contentResolver, selectedPhotoUri)
                val folderName = "recipes_images"  // יכול לשנות את שם התיקייה אם רוצים
                val imageName = "${UUID.randomUUID()}.jpg"

                // קריאה ל-CloudinaryModel להעלות את התמונה
                CloudinaryModel.uploadImage(
                    bitmap,
                    imageName,
                    folderName,
                    onSuccess = { imageUrl ->
                        saveRecipeToFirestore(title, description, imageUrl)
                    },
                    onError = { error ->
                        Log.e("AddRecipeFragment", "Image upload failed: $error")
                        Toast.makeText(requireContext(), "Failed to upload image", Toast.LENGTH_SHORT).show()
                    }
                )
            } catch (e: Exception) {
                Log.e("AddRecipeFragment", "Error during image upload: ${e.message}")
                Toast.makeText(requireContext(), "Failed to upload image", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveRecipeToFirestore(title: String, description: String, imageUrl: String) {
        val latitude = currentLocation?.latitude
        val longitude = currentLocation?.longitude

        val newRecipe = Recipe(
            name = title,
            description = description,
            rating = 4.0f,
            imageUrl = imageUrl,
            latitude = latitude,
            longitude = longitude
        )

        val db = FirebaseFirestore.getInstance()
        db.collection("recipes")
            .add(newRecipe)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Recipe saved successfully", Toast.LENGTH_SHORT).show()
                requireActivity().supportFragmentManager.popBackStack()
            }
            .addOnFailureListener { e ->
                Log.e("AddRecipeFragment", "Error saving recipe: ${e.message}")
                Toast.makeText(requireContext(), "Error saving recipe", Toast.LENGTH_SHORT).show()
            }
    }


    companion object {
        const val CAMERA_PERMISSION_CODE = 1001
    }
}
