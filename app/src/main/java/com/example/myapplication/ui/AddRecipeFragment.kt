package com.example.myapplication.ui

import StepsAdapter
import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import android.view.*
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.data.remote.CloudinaryModel
import com.example.myapplication.model.Recipe
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.*
import java.io.File
import java.util.*

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
    private lateinit var loadingOverlay: View


    companion object {
        const val CAMERA_PERMISSION_CODE = 1001
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add_recipe, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        setupToolbar(view)
        initializeViews(view)
        setupAdapters()
        setupListeners()
        getLastKnownLocation()
        loadingOverlay = view.findViewById(R.id.loading_overlay)
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
            findNavController().navigateUp()
        }
    }

    private fun initializeViews(view: View) {
        recipeTitleInput = view.findViewById(R.id.recipe_title_input)
        ingredientsRecyclerView = view.findViewById(R.id.ingredients_recycler_view)
        stepsRecyclerView = view.findViewById(R.id.steps_recycler_view)
        saveRecipeButton = view.findViewById(R.id.save_recipe_button)
        photoImageView = view.findViewById(R.id.photo_image_view)
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
                Toast.makeText(requireContext(), "Please enter a recipe title", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val description = steps.joinToString(separator = "\n")

            if (selectedPhotoUri != null) {
                uploadImageAndSaveRecipe(title, description, ingredients)
            } else {
                saveRecipeToFirestore(title, description, imageUrl = "", ingredients = listOf<String>(""))
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

    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                selectedPhotoUri = it
                photoImageView.setImageURI(it)
                Toast.makeText(requireContext(), "Photo selected from gallery", Toast.LENGTH_SHORT).show()
            }
        }

    private val cameraLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                selectedPhotoUri = cameraPhotoUri
                photoImageView.setImageURI(cameraPhotoUri)
                Toast.makeText(requireContext(), "Photo captured", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Camera action canceled", Toast.LENGTH_SHORT).show()
            }
        }

    private fun checkAndRequestCameraPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_CODE)
        } else {
            capturePhotoFromCamera()
        }
    }

    private fun capturePhotoFromCamera() {
        val photoFile = createImageFile()
        cameraPhotoUri = FileProvider.getUriForFile(requireContext(), "com.example.myapplication.fileprovider", photoFile)
        cameraLauncher.launch(cameraPhotoUri)
    }

    private fun createImageFile(): File {
        return File.createTempFile("photo_", ".jpg", requireContext().cacheDir)
    }

    @SuppressLint("MissingPermission")
    private fun getLastKnownLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 101)
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            currentLocation = location
        }
    }

    private fun uploadImageAndSaveRecipe(title: String, description: String, ingredients: List<String>) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(requireContext().contentResolver, selectedPhotoUri)
                val folderName = "recipes_images"
                val imageName = "${UUID.randomUUID()}.jpg"

                CloudinaryModel.uploadImage(
                    bitmap,
                    imageName,
                    folderName,
                    onSuccess = { imageUrl ->
                        saveRecipeToFirestore(title, description, imageUrl, ingredients)
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

    private fun saveRecipeToFirestore(title: String, description: String, imageUrl: String, ingredients: List<String>) {
        showLoading()
        val latitude = currentLocation?.latitude
        val longitude = currentLocation?.longitude
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        val newRecipe = Recipe(
            name = title,
            description = description,
            rating = 4.0f,
            imageUrl = imageUrl,
            latitude = latitude,
            longitude = longitude,
            ownerId = currentUserId,
            ingredients = ingredients
        )


        FirebaseFirestore.getInstance().collection("recipes")
            .add(newRecipe)
            .addOnSuccessListener {
                Handler(Looper.getMainLooper()).postDelayed({
                    hideLoading()
                    Toast.makeText(context, "Recipe saved!", Toast.LENGTH_SHORT).show()
                    findNavController().navigateUp()
                }, 1000) // עיכוב של 1 שנייה
            }

            .addOnFailureListener { e ->
                hideLoading()
                Log.e("AddRecipeFragment", "Error saving recipe: ${e.message}")
                Toast.makeText(requireContext(), "Error saving recipe", Toast.LENGTH_SHORT).show()
            }
    }

    private fun showLoading() {
        loadingOverlay.visibility = View.VISIBLE
    }

    private fun hideLoading() {
        loadingOverlay.visibility = View.GONE
    }

}
