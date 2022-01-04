package com.bassem.clinic_userapp.ui.home

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bassem.clinic_userapp.R
import com.bassem.clinic_userapp.databinding.HomeFragmentBinding
import com.google.firebase.firestore.FirebaseFirestore

class Home() : Fragment(R.layout.home_fragment) {
    var _binding: HomeFragmentBinding? = null
    val binding get() = _binding
    var db: FirebaseFirestore? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = HomeFragmentBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        GettingData()
    }

    fun GettingData() {
        val sharedPreferences = activity?.getSharedPreferences("PREF", Context.MODE_PRIVATE)
        val id = sharedPreferences?.getString("id", "")
        db = FirebaseFirestore.getInstance()
        db?.collection("patiens_info")?.document(id!!)?.addSnapshotListener { value, error ->
            if (error != null) {
                println(error.message)
            } else {
                if (value?.getString("pre") != null) {
                    binding?.roshta?.visibility = View.VISIBLE
                    binding?.preTV?.text = value.getString("pre")
                }
                if (value?.getString("ins") != null) {
                    binding?.roshta?.visibility = View.VISIBLE
                    binding?.insTV?.text = value.getString("ins")
                }
                val sex = value?.getString("sex")
                if (sex.equals("male")) {
                    binding?.name?.text = "Hello Mr ${value?.getString("fullname")}"

                } else {
                    binding?.name?.text = "Hello Miss ${value?.getString("fullname")}"
                }
                if (value?.getBoolean("hasVisit")==true){
                    binding?.upcomingCard?.visibility=View.VISIBLE
                    binding?.next?.text=value.getString("next_visit")
                }

            }
        }

    }
}