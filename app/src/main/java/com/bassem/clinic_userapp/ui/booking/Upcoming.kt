package com.bassem.clinic_userapp.ui.booking

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.bassem.clinic_userapp.R
import com.bassem.clinic_userapp.databinding.UpcomingFragmentBinding
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.upcoming_fragment.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.Calendar
import kotlin.collections.HashMap

class Upcoming() : Fragment(R.layout.upcoming_fragment) {

    var _binding: UpcomingFragmentBinding? = null
    val binding get() = _binding
    var db: FirebaseFirestore? = null
    var id: String? = null
    var visit_id: String? = null
    var today: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sharedPreferences = activity?.getSharedPreferences("PREF", Context.MODE_PRIVATE)
        id = sharedPreferences?.getString("id", "")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = UpcomingFragmentBinding.inflate(inflater, container, false)
        return binding?.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getData()



        binding?.newbooking?.setOnClickListener {
            val navController = Navigation.findNavController(activity!!, R.id.nav_host_fragment)
            navController.navigate(R.id.action_booking_to_calendar2)
        }
        binding?.cancel?.setOnClickListener {
            Cancel()
        }

    }


    fun Cancel() {
        println("Cancel")
        db = FirebaseFirestore.getInstance()
        var cancel = HashMap<String, Any>()
        db?.collection("patiens_info")?.document(id!!)?.update("IsVisit", false)
            ?.addOnCompleteListener {

                if (it.isSuccessful) {

                    binding?.newbooking?.visibility = View.VISIBLE
                    binding?.visitcard?.visibility = View.GONE
                    CancelOnVisit()

                }

            }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getData() {
        println("Data")
        db = FirebaseFirestore.getInstance()
        db?.collection("patiens_info")?.document(id!!)?.addSnapshotListener { value, error ->
            if (error != null) {
                println("${error.message} ERROR")
            } else {
                val isVisit = value?.getBoolean("IsVisit")
                val nextVisit = value?.getString("next_visit")
                if (isVisit!! && IsBookedPassed(nextVisit!!)) {
                    binding?.visitcard?.visibility = View.VISIBLE
                    binding?.newbooking?.visibility = View.GONE

                    binding?.nextapp?.text = value.getString("next_visit")
                    binding?.notes?.text = value.getString("note")
                    binding?.requests?.text = value.getString("req")
                    visit_id = value.getString("visit_id")
                    binding?.timeUpcoming?.text = value.getString("visit_time")
                    println("Visit $visit_id")
                } else {

                    binding?.visitcard?.visibility = View.GONE
                    binding?.newbooking?.visibility = View.VISIBLE
                }

            }
        }

    }

    fun CancelOnVisit() {
        db = FirebaseFirestore.getInstance()
        var cancelHashMap = HashMap<String, Any>()
        cancelHashMap["status"] = "cancelled by you"
        cancelHashMap["date"] = today!!
        db!!.collection("visits").document(visit_id!!).update(cancelHashMap)
            .addOnCompleteListener {
                println("BY YOU")

            }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun IsBookedPassed(visit: String): Boolean {
        val locale = Locale.ENGLISH
        val sdf = DateTimeFormatter.ofPattern("d-M-yyyy", locale)
        val visitDate: LocalDate = LocalDate.parse(visit, sdf)
        val dateNow = LocalDate.now()
        return visitDate >= dateNow
    }

    fun GetToday() {
        val calendar: Calendar = Calendar.getInstance()
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val month = calendar.get(Calendar.MONTH) + 1
        val year = calendar.get(Calendar.YEAR)
        today = "$day-$month-$year"
    }


}