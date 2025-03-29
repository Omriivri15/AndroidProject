package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.data.config.CloudinaryConfig
import com.example.myapplication.data.remote.CloudinaryModel
import com.example.myapplication.data.remote.FirebaseAuthManager
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // בדיקת התחברות
        val firebaseAuth = FirebaseAuthManager
        if (firebaseAuth.getCurrentUser() == null) {
            navigateToLoginActivity()
            return
        }

        setContentView(R.layout.activity_main)

        // אתחול Cloudinary
        initCloudinary()

        // אין צורך ב־displayFragment - הניווט נעשה ע"י NavGraph
    }

    private fun initCloudinary() {
        CloudinaryModel.init(
            this,
            CloudinaryConfig.CLOUD_NAME,
            CloudinaryConfig.API_KEY,
            CloudinaryConfig.API_SECRET
        )
    }

    private fun navigateToLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}
