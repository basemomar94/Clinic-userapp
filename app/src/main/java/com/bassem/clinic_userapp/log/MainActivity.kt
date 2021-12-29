package com.bassem.clinic_userapp.log

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

import android.view.View.inflate
import android.widget.Button
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.bassem.clinic_userapp.R
import com.bassem.clinic_userapp.ui.Container
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

         login.setOnClickListener {
             login.text=""
             loading.visibility=View.VISIBLE
             if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                 login.setAllowClickWhenDisabled(false)
             }
             login.alpha=.5F
             gotohome()
         }
    }

    fun gotohome(){
        val intent:Intent= Intent(this,Container::class.java)
        startActivity(intent)
    }

}