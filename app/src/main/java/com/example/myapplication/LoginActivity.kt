package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class LoginActivity : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth
    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        firebaseAuth = FirebaseAuth.getInstance()

        // בדיקה אם המשתמש מחובר
        if (firebaseAuth.currentUser != null) {
            navigateToMainActivity()
        }

        // View bindings
        val loginView = findViewById<View>(R.id.loginView)
        val registerView = findViewById<View>(R.id.registerView)

        val switchToRegister = findViewById<TextView>(R.id.switchToRegister)
        val switchToLogin = findViewById<TextView>(R.id.switchToLogin)

        val loginButton = findViewById<Button>(R.id.loginButton)
        val registerButton = findViewById<Button>(R.id.registerButton)

        val loginEmail = findViewById<EditText>(R.id.emailEditText)
        val loginPassword = findViewById<EditText>(R.id.passwordEditText)
        val registerEmail = findViewById<EditText>(R.id.registerEmailEditText)
        val registerPassword = findViewById<EditText>(R.id.registerPasswordEditText)
        val registerFullName = findViewById<EditText>(R.id.fullNameEditText)

        // מעבר למסך הרשמה
        switchToRegister.setOnClickListener {
            loginView.visibility = View.GONE
            registerView.visibility = View.VISIBLE
        }

        // מעבר למסך התחברות
        switchToLogin.setOnClickListener {
            loginView.visibility = View.VISIBLE
            registerView.visibility = View.GONE
        }

        // התחברות
        loginButton.setOnClickListener {
            val email = loginEmail.text.toString().trim()
            val password = loginPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Login Successful!", Toast.LENGTH_SHORT).show()
                        navigateToMainActivity()
                    } else {
                        Toast.makeText(this, "Login Failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        }

        // **הרשמה ושמירה במסד הנתונים**
        registerButton.setOnClickListener {
            val fullName = registerFullName.text.toString().trim()
            val email = registerEmail.text.toString().trim()
            val password = registerPassword.text.toString().trim()

            if (fullName.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val userId = firebaseAuth.currentUser?.uid

                        if (userId == null) {
                            Toast.makeText(this, "Error: User ID is null!", Toast.LENGTH_SHORT).show()
                            return@addOnCompleteListener
                        }

                        val user = hashMapOf(
                            "userId" to userId,
                            "fullName" to fullName,
                            "email" to email,
                            "timestamp" to System.currentTimeMillis()
                        )

                        firestore.collection("users").document(userId)
                            .set(user)
                            .addOnSuccessListener {
                                Toast.makeText(this, "Registration Successful!", Toast.LENGTH_SHORT).show()
                                navigateToMainActivity()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(this, "Failed to save user data: ${e.message}", Toast.LENGTH_SHORT).show()
                            }

                    } else {
                        Toast.makeText(this, "Registration Failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}
