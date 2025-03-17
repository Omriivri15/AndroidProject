package com.example.myapplication.data.remote

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

object FirebaseAuthManager {

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    fun getCurrentUser(): FirebaseUser? {
        return firebaseAuth.currentUser
    }

    fun signIn(email: String, password: String, onSuccess: (FirebaseUser) -> Unit, onError: (String) -> Unit) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    firebaseAuth.currentUser?.let { onSuccess(it) }
                } else {
                    onError(task.exception?.message ?: "Login failed")
                }
            }
    }

    fun signUp(email: String, password: String, onSuccess: (FirebaseUser) -> Unit, onError: (String) -> Unit) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    firebaseAuth.currentUser?.let { onSuccess(it) }
                } else {
                    onError(task.exception?.message ?: "Signup failed")
                }
            }
    }

    fun signOut() {
        firebaseAuth.signOut()
    }
}
