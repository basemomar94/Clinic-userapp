package com.bassem.clinic_userapp.ui.booking

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bassem.clinic_userapp.R
import com.bassem.clinic_userapp.databinding.VisitExpandBinding
import com.google.firebase.firestore.FirebaseFirestore

class VisitsExpand() : Fragment(R.layout.visit_expand) {
    private var _binding: VisitExpandBinding? = null
    private val binding get() = _binding
    private var visit: String? = null
    private var db: FirebaseFirestore? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var bundle = this.arguments
        if (bundle != null) {
            visit = bundle.getString("visit")
            println("$visit Got")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = VisitExpandBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        GettingData()
    }

    private fun GettingData() {
        db = FirebaseFirestore.getInstance()
        db!!.collection("visits").document(visit!!).addSnapshotListener { value, error ->
            if (error != null) {
                println(error.message)
            } else {
                binding?.BookedDate?.text = value?.getString("date")
                binding?.BookedBy?.text = "Booked by ${value?.getString("Booked_by")}"
                var status = value?.getString("status")
                if (status.equals("completed")) {
                    binding?.done1?.visibility = View.VISIBLE
                    binding?.done2?.visibility = View.VISIBLE
                    binding?.done3?.visibility = View.VISIBLE
                    binding?.visitMed?.text = value?.getString("pre")
                    binding?.visitAdvice?.text = value?.getString("ins")
                    binding?.visitReq?.text = value?.getString("req")

                } else {
                    binding?.statusL?.visibility = View.VISIBLE
                    binding?.bookingStatus?.text = status

                }
            }
        }

    }
}