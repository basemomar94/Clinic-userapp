package com.bassem.clinic_userapp.log

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
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
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onStart() {
        super.onStart()
        auth= FirebaseAuth.getInstance()
        val currentUser= auth.currentUser

        if (currentUser!=null){
            gotohome()
        }
    }

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
             Signin()
         }
    }

    fun gotohome(){
        val intent:Intent= Intent(this,Container::class.java)
        startActivity(intent)
    }

    fun Signin(){
        auth.signInWithEmailAndPassword(mail_log.text.toString().trim(),password_log.text.toString().trim()).addOnCompleteListener {
            task->
            if (task.isSuccessful){
                val id=auth.currentUser?.uid
                var sharedPreferences:SharedPreferences=getSharedPreferences("PREF",Context.MODE_PRIVATE)
                var editor=sharedPreferences.edit()
                editor.putString("id",id)
                editor.commit()
                gotohome()
            }
        }

    }

}