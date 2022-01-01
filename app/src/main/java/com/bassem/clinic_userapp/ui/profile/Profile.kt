package com.bassem.clinic_userapp.ui.profile

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bassem.clinic_userapp.R
import com.bassem.clinic_userapp.databinding.ProfileFragmentBinding
import com.google.firebase.firestore.FirebaseFirestore

class Profile() : Fragment(R.layout.profile_fragment) {
    var _binding: ProfileFragmentBinding? = null
    val binding get() = _binding
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = ProfileFragmentBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        GettingData()
    }

    fun GettingData() {
        val sharedPreferences: SharedPreferences =
            activity!!.getSharedPreferences("PREF", Context.MODE_PRIVATE)
        val id = sharedPreferences.getString("id", "noo")!!
        db = FirebaseFirestore.getInstance()
        db.collection("patiens_info").document(id).addSnapshotListener { value, error ->

            if (error != null) {
                println("Firebase error ${error.message}")
            } else {
                binding!!.fullnameInfo.text = value?.getString("fullname")
                binding!!.ageInfo.text = value?.getDouble("age")?.toInt().toString()
                binding!!.jobInfo.text = value?.getString("job")
                binding!!.complainInfo.text = value?.getString("complain")
                binding!!.mailInfo.text = value?.getString("mail")

                binding!!.phoneInfo.text = value?.getString("phone")

                binding!!.notesInfo.text = value?.getString("note")
                val sex = value?.getString("sex")
                binding!!.sexInfo.text = sex


            }
        }
    }


}