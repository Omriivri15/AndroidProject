package com.example.myapplication

import com.example.myapplication.ui.RecipesListFragment  // שים לב לייבוא הנכון
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.myapplication.data.config.CloudinaryConfig
import com.example.myapplication.data.remote.CloudinaryModel
import com.example.myapplication.data.remote.FirebaseAuthManager
import com.example.myapplication.ui.AddRecipeFragment
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    private var inDisplayFragment: Fragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // אתחול Firebase
        val firebaseAuth = FirebaseAuthManager

        // בדיקה אם המשתמש מחובר, אם לא - מעבר למסך ההתחברות
        if (firebaseAuth.getCurrentUser() == null) {
            navigateToLoginActivity()
            return
        }

        setContentView(R.layout.activity_main)

        // אתחול Cloudinary עם המפתחות מקובץ הקונפיגורציה
        initCloudinary()

        // הצגת רשימת המתכונים כברירת מחדל
        if (savedInstanceState == null) {
            displayFragment(RecipesListFragment())
        }
    }

    // אתחול Cloudinary מאובטח
    private fun initCloudinary() {
        CloudinaryModel.init(
            this,
            CloudinaryConfig.CLOUD_NAME,
            CloudinaryConfig.API_KEY,
            CloudinaryConfig.API_SECRET
        )
    }

    // פונקציה להחלפת פרגמנט
    fun displayFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.main_activity_frame_layout, fragment)
            addToBackStack(null)
            commit()
        }
        inDisplayFragment = fragment
    }

    // מעבר למסך ההתחברות אם המשתמש לא מחובר
    private fun navigateToLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}
