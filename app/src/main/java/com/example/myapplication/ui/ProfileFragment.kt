package com.example.myapplication.ui

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.example.myapplication.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso

class ProfileFragment : Fragment() {

    private lateinit var profileImageView: ImageView
    private lateinit var nameTextView: TextView
    private lateinit var editNameEditText: EditText
    private lateinit var uploadPhotoButton: Button
    private lateinit var saveButton: Button

    private val firebaseAuth = FirebaseAuth.getInstance()
    private val databaseRef = FirebaseDatabase.getInstance().reference.child("users")
    private val storageRef = FirebaseStorage.getInstance().reference.child("profile_photos")

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

        // Load user info
        loadUserInfo()

        // Upload photo logic
        uploadPhotoButton.setOnClickListener {
            imagePickerLauncher.launch("image/*")
        }

        // Save user info logic
        saveButton.setOnClickListener {
            saveUserInfo()
        }
    }

    private val imagePickerLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            selectedPhotoUri = it
            profileImageView.setImageURI(it) // Display the selected image in the ImageView
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
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    private fun loadUserInfo() {
        val userId = firebaseAuth.currentUser?.uid ?: return

        // Fetch user data from Firebase Database
        databaseRef.child(userId).get().addOnSuccessListener { snapshot ->
            val name = snapshot.child("name").value as? String
            val photoUrl = snapshot.child("photoUrl").value as? String

            nameTextView.text = name
            editNameEditText.setText(name)

            if (!photoUrl.isNullOrEmpty()) {
                Picasso.get().load(photoUrl).into(profileImageView)
            }
        }.addOnFailureListener {
            Toast.makeText(requireContext(), "Failed to load user info", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveUserInfo() {
        val userId = firebaseAuth.currentUser?.uid ?: return
        val newName = editNameEditText.text.toString()

        if (newName.isBlank()) {
            Toast.makeText(requireContext(), "Name cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }

        val userUpdates = mutableMapOf<String, Any>("name" to newName)

        // If a new photo is selected, upload it to Firebase Storage
        selectedPhotoUri?.let { uri ->
            val photoRef = storageRef.child("$userId.jpg")
            photoRef.putFile(uri).addOnSuccessListener {
                photoRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                    userUpdates["photoUrl"] = downloadUrl.toString()
                    updateUserDatabase(userId, userUpdates)
                }
            }.addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to upload photo", Toast.LENGTH_SHORT).show()
            }
        } ?: run {
            updateUserDatabase(userId, userUpdates)
        }
    }

    private fun updateUserDatabase(userId: String, updates: Map<String, Any>) {
        databaseRef.child(userId).updateChildren(updates).addOnSuccessListener {
            Toast.makeText(requireContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText(requireContext(), "Failed to update profile", Toast.LENGTH_SHORT).show()
        }
    }
}
