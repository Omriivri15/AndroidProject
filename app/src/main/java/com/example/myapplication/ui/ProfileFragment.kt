package com.example.myapplication.ui

import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.adapter.RecipeAdapter
import com.example.myapplication.model.Recipe
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso

class ProfileFragment : Fragment() {

    private lateinit var profileImageView: ImageView
    private lateinit var nameTextView: TextView
    private lateinit var editNameEditText: EditText
    private lateinit var uploadPhotoButton: Button
    private lateinit var saveButton: Button
    private lateinit var recipesRecyclerView: RecyclerView
    private lateinit var recipeAdapter: RecipeAdapter
    private val recipeList = mutableListOf<Recipe>()

    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    val storage = FirebaseStorage.getInstance()
    val storageRef = storage.reference

    private var selectedPhotoUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar(view)

        profileImageView = view.findViewById(R.id.profile_image_view)
        nameTextView = view.findViewById(R.id.name_text_view)
        editNameEditText = view.findViewById(R.id.edit_name_edit_text)
        uploadPhotoButton = view.findViewById(R.id.upload_photo_button)
        saveButton = view.findViewById(R.id.save_button)

        recipesRecyclerView = view.findViewById(R.id.user_recipes_recycler_view)
        recipesRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        recipeAdapter = RecipeAdapter(recipeList, firebaseAuth.currentUser?.uid ?: "")
        recipesRecyclerView.adapter = recipeAdapter

        loadUserInfo()
        loadUserRecipes()

        uploadPhotoButton.setOnClickListener {
            imagePickerLauncher.launch("image/*")
        }

        saveButton.setOnClickListener {
            saveUserInfo()
        }
    }

    private val imagePickerLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            selectedPhotoUri = it
            profileImageView.setImageURI(it)
        }
    }

    private fun setupToolbar(view: View) {
        val toolbar: Toolbar = view.findViewById(R.id.toolbar)
        (activity as AppCompatActivity).apply {
            setSupportActionBar(toolbar)
            supportActionBar?.apply {
                title = "My Profile"
                setDisplayHomeAsUpEnabled(true)
            }
        }

        toolbar.setNavigationOnClickListener {
            findNavController().navigateUp() // ← עדכון חשוב!
        }
    }

    private fun loadUserInfo() {
        val userId = firebaseAuth.currentUser?.uid ?: return

        firestore.collection("users").document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val name = document.getString("fullName") ?: "No Name"
                    val photoUrl = document.getString("photoUrl")

                    nameTextView.text = name
                    editNameEditText.setText(name)

                    if (!photoUrl.isNullOrEmpty()) {
                        Picasso.get().load(photoUrl).into(profileImageView)
                    }
                }
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to load user info", Toast.LENGTH_SHORT).show()
            }
    }

    private fun loadUserRecipes() {
        val userId = firebaseAuth.currentUser?.uid ?: return

        firestore.collection("recipes")
            .whereEqualTo("ownerId", userId)
            .get()
            .addOnSuccessListener { snapshot ->
                recipeList.clear()
                for (doc in snapshot.documents) {
                    val recipe = doc.toObject(Recipe::class.java)
                    recipe?.id = doc.id // ← זה השינוי החשוב
                    recipe?.let { recipeList.add(it) }
                }
                recipeAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to load recipes", Toast.LENGTH_SHORT).show()
            }
    }


    private fun saveUserInfo() {
        val userId = firebaseAuth.currentUser?.uid ?: return
        val newName = editNameEditText.text.toString().trim()

        if (newName.isBlank()) {
            Toast.makeText(requireContext(), "Name cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }

        val userUpdates = hashMapOf<String, Any>(
            "fullName" to newName,
            "email" to (firebaseAuth.currentUser?.email ?: ""),
            "timestamp" to System.currentTimeMillis()
        )

        saveButton.isEnabled = false
        saveButton.text = "Saving..."

        val uri = selectedPhotoUri

        if (uri != null) {
            try {
                // מוודא שהתמונה זמינה לקריאה
                val inputStream = requireContext().contentResolver.openInputStream(uri)
                inputStream?.close()

                val photoRef = FirebaseStorage.getInstance()
                    .reference
                    .child("profile_photos/$userId.jpg")

                photoRef.putFile(uri)
                    .addOnSuccessListener {
                        photoRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                            userUpdates["photoUrl"] = downloadUrl.toString()
                            updateUserInFirestore(userId, userUpdates)
                        }.addOnFailureListener { err ->
                            Toast.makeText(requireContext(), "Failed to get download URL: ${err.message}", Toast.LENGTH_SHORT).show()
                            resetSaveButton()
                        }
                    }
                    .addOnFailureListener { err ->
                        Toast.makeText(requireContext(), "Failed to upload photo: ${err.message}", Toast.LENGTH_SHORT).show()
                        resetSaveButton()
                    }

            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Failed to access image: ${e.message}", Toast.LENGTH_LONG).show()
                resetSaveButton()
            }
        } else {
            updateUserInFirestore(userId, userUpdates)
        }
    }


    private fun updateUserInFirestore(userId: String, updates: Map<String, Any>) {
        firestore.collection("users").document(userId)
            .set(updates, SetOptions.merge())
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Profile updated successfully!", Toast.LENGTH_SHORT).show()
                resetSaveButton()
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to update profile", Toast.LENGTH_SHORT).show()
                resetSaveButton()
            }
    }

    private fun resetSaveButton() {
        saveButton.isEnabled = true
        saveButton.text = "Save Changes"
    }
}
