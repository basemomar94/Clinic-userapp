package com.bassem.clinic_userapp.ui.booking

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bassem.clinic_userapp.R
import com.bassem.clinic_userapp.databinding.UpcomingFragmentBinding
import com.google.firebase.firestore.FirebaseFirestore

class Upcoming() : Fragment(R.layout.upcoming_fragment) {

    var _binding: UpcomingFragmentBinding? = null
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
        _binding = UpcomingFragmentBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.newbooking?.setOnClickListener {
            findNavController().navigate(R.id.action_booking_to_calendar2)
        }
        getData()
    }

    fun getData() {
        val sharedPreferences = activity?.getSharedPreferences("PREF", Context.MODE_PRIVATE)
        val id = sharedPreferences?.getString("id", "")
        db?.collection("patiens_info")?.document(id!!)?.addSnapshotListener { value, error ->

            if (error != null) {
                binding?.nextapp?.text = value?.getString("next_visit")
                binding?.notes?.text = value?.getString("note")
                binding?.requests?.text=value?.getString("req")
                println(value?.getString("next_visit"))
            }
        }

    }

}