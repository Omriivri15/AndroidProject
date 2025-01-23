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

class LoginActivity : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        firebaseAuth = FirebaseAuth.getInstance()

        // Check if the user is already logged in
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

        // Switch to Register View
        switchToRegister.setOnClickListener {
            loginView.visibility = View.GONE
            registerView.visibility = View.VISIBLE
        }

        // Switch to Login View
        switchToLogin.setOnClickListener {
            loginView.visibility = View.VISIBLE
            registerView.visibility = View.GONE
        }

        // Login Button Logic
        loginButton.setOnClickListener {
            val email = loginEmail.text.toString()
            val password = loginPassword.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Login Successful!", Toast.LENGTH_SHORT).show()
                            navigateToMainActivity()
                        } else {
                            Toast.makeText(this, "Login Failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }

        // Register Button Logic
        registerButton.setOnClickListener {
            val email = registerEmail.text.toString()
            val password = registerPassword.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Registration Successful!", Toast.LENGTH_SHORT).show()
                            loginView.visibility = View.VISIBLE
                            registerView.visibility = View.GONE
                        } else {
                            Toast.makeText(this, "Registration Failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}
