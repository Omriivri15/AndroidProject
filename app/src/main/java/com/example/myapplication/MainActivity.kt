package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment

class MainActivity : AppCompatActivity() {
    private var fragmentOne:RecipesListFragment?=null
    private var buttonOne:Button?=null


    private var inDisplayFragment:Fragment?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        fragmentOne=RecipesListFragment()
        buttonOne=findViewById(R.id.main_activity_button_one)


        buttonOne?.setOnClickListener({
            if (inDisplayFragment!=null){
                supportFragmentManager.beginTransaction().remove(inDisplayFragment!!).commit()
                inDisplayFragment=null
            }
            else {
                display(fragmentOne)
            }

        })



    }

    fun display(fragment: Fragment?){
        fragment?.let {
            supportFragmentManager.beginTransaction().apply {
                inDisplayFragment?.let {
                    remove(it)
                }
                add(R.id.main_activity_frame_layout,it)
                addToBackStack("TAG")
                commit()
            }
            inDisplayFragment=fragment
        }


    }











}