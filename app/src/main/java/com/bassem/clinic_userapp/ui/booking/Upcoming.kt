package com.bassem.clinic_userapp.ui.booking

import android.app.AlertDialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.bassem.clinic_userapp.R
import com.bassem.clinic_userapp.databinding.UpcomingFragmentBinding
import com.google.firebase.firestore.FirebaseFirestore
import java.time.LocalDate
import java.time.LocalTime
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
    var fees: String? = null
    var open: String? = null
    var close: String? = null
    var available: Boolean = true


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sharedPreferences = activity?.getSharedPreferences("PREF", Context.MODE_PRIVATE)
        id = sharedPreferences?.getString("id", "")
        GetToday()
        GetSettings()

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
            val builder = AlertDialog.Builder(context)
            builder.setMessage("Are you sure to cancel your appointment")
            builder.setPositiveButton("yes") { builder, _ -> Cancel() }
            builder.setNegativeButton("No") { builder, _ -> builder.dismiss() }
            builder.setTitle("Cancel booking")
            builder.show()

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
                if (isVisit!! && IsBookDatePassed(nextVisit!!)) {
                    binding?.visitcard?.visibility = View.VISIBLE
                    binding?.newbooking?.visibility = View.GONE
                    binding?.noBookinglayout?.visibility = View.GONE



                    binding?.nextapp?.text = value.getString("next_visit")
                    binding?.notes?.text = value.getString("note")
                    binding?.requests?.text = value.getString("req")
                    visit_id = value.getString("visit_id")
                    binding?.timeUpcoming?.text = value.getString("visit_time")
                    binding?.feesUpcoming?.text = fees

                } else {

                    binding?.visitcard?.visibility = View.GONE
                    binding?.noBookinglayout?.visibility = View.VISIBLE
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
    fun IsBookDatePassed(visit: String): Boolean {
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

    @RequiresApi(Build.VERSION_CODES.O)
    fun GetSettings() {
        db = FirebaseFirestore.getInstance()
        val locale = Locale.ENGLISH
        var timeNow = LocalTime.now()
        val sdf = DateTimeFormatter.ofPattern("hh:mm a", locale)
        db!!.collection("settings").document("settings").addSnapshotListener { value, error ->
            if (error != null) {
                println(error.message)
            } else {
                fees = value?.getString("fees")
                open = value!!.getString("open")!!
                close = value.getString("close")!!
                val openTime = LocalTime.parse(open!!.trim(), sdf)
                val closeTime = LocalTime.parse(close!!.trim(), sdf)
                available = timeNow > openTime && timeNow < closeTime
            }
        }
    }


}