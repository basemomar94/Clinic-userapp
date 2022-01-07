package com.bassem.clinic_userapp.log

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

import android.widget.Toast
import com.bassem.clinic_userapp.R
import com.bassem.clinic_userapp.ui.Container
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

         confirmBu.setOnClickListener {
             confirmBu.text=""
             loading.visibility=View.VISIBLE
            loading.isClickable=false
             confirmBu.alpha=.5F
             Signin()
         }
    }

    fun gotohome(){
        val intent:Intent= Intent(this,Container::class.java)
        startActivity(intent)
        finish()
    }

    fun Signin(){
        if (mail_log.text.isNotEmpty()&&password_log.text.isNotEmpty()){
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
            }.addOnFailureListener {
                Toast.makeText(this,"${it.message}",Toast.LENGTH_LONG).show()
                confirmBu.text="Login"
                loading.visibility=View.INVISIBLE
                loading.isClickable=true
                confirmBu.alpha=1F

            }
        } else {
            Toast.makeText(this,"Please enter your mail and password ",Toast.LENGTH_LONG).show()
            confirmBu.text="Login"
            loading.visibility=View.INVISIBLE
            loading.isClickable=true
            confirmBu.alpha=1F
        }


    }

}