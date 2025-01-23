package com.example.myapplication

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.myapplication.ui.AddRecipeFragment
import com.example.myapplication.ui.RecipesListFragment
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {
    private var inDisplayFragment: Fragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // הצגת הפיד של המתכונים כברירת מחדל
        if (savedInstanceState == null) {
            displayFragment(RecipesListFragment())
        }

        // הגדרת כפתור ה-FAB למעבר ל-AddRecipeFragment
        val fab: FloatingActionButton = findViewById(R.id.fab_add_recipe)
        fab.setOnClickListener {
            displayFragment(AddRecipeFragment())
        }
    }

    // פונקציה להחלפת פרגמנטים
    private fun displayFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.main_activity_frame_layout, fragment)
            addToBackStack(null)
            commit()
        }
        inDisplayFragment = fragment
    }
}
