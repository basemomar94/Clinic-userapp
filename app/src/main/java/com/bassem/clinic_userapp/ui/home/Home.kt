package com.bassem.clinic_userapp.ui.home

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bassem.clinic_userapp.R
import com.bassem.clinic_userapp.databinding.HomeFragmentBinding
import com.bassem.clinic_userapp.ui.booking.Visits
import com.google.firebase.firestore.*
import com.google.firebase.firestore.EventListener
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

class Home() : Fragment(R.layout.home_fragment) {
    var _binding: HomeFragmentBinding? = null
    val binding get() = _binding
    var db: FirebaseFirestore? = null
    var name: String? = null
    var complain: String? = null
    var nextvist: String? = null
    var today: String? = null
    var visitsArrayList: ArrayList<Visits>? = null
    var turn: String? = null
    var totalCompleteVisits: Int? = null
    var nextDate: String? = null
    var nextTime: String? = null
    var waiting: Int? = null


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        GetToday()
        GetSettings()

//
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = HomeFragmentBinding.inflate(inflater, container, false)
        return binding?.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        GettingData()
        binding?.upcomingCard?.setOnClickListener {
            findNavController().navigate(R.id.action_home_to_booking)

        }
        binding?.bookHome?.setOnClickListener {
            findNavController().navigate(R.id.action_home_to_calendar2)
        }


    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun GettingData() {
        val sharedPreferences = activity?.getSharedPreferences("PREF", Context.MODE_PRIVATE)
        val id = sharedPreferences?.getString("id", "")
        db = FirebaseFirestore.getInstance()
        db?.collection("patiens_info")?.document(id!!)?.addSnapshotListener { value, error ->
            if (error != null) {
                println(error.message)
            } else {
                if (value?.getString("pre") != null) {
                    binding?.welcome?.visibility=View.GONE
                    binding?.roshta?.visibility = View.VISIBLE
                    binding?.preTV?.text = value.getString("pre")
                }
                if (value?.getString("ins") != null) {
                    binding?.welcome?.visibility=View.GONE
                    binding?.roshta?.visibility = View.VISIBLE
                    binding?.insTV?.text = value.getString("ins")
                }
                val sex = value?.getString("sex")
                if (sex.equals("male")) {
                    binding?.name?.text = "Hello Mr ${value?.getString("fullname")}"

                } else {
                    binding?.name?.text = "Hello Miss ${value?.getString("fullname")}"
                }
                if (value?.getBoolean("IsVisit") == true) {
                    binding?.welcome?.visibility=View.GONE
                    binding?.upcomingCard?.visibility = View.VISIBLE
                    nextDate = value.getString("next_visit")
                    nextTime = value.getString("visit_time")
                    binding?.next?.text=nextDate
                    binding?.timeHome?.text=nextTime
                    IsClinicOpen()


                }
                name = value?.getString("fullname")
                complain = value?.getString("complain")
                turn = value?.getString("turn")
                binding?.patientNumber?.text = turn
                val editor = sharedPreferences.edit()
                editor.putString("name", name)
                editor.putString("complain", complain)
                editor.apply()

            }
        }

    }

    fun GetToday() {
        val calendar: Calendar = Calendar.getInstance()
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val month = calendar.get(Calendar.MONTH) + 1
        val year = calendar.get(Calendar.YEAR)
        today = "$day-$month-$year"
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun Show_turn_Card() {
        if (nextDate == today) {
            GetTurnsData()

            binding?.numberCard?.visibility = View.VISIBLE
            binding?.upcomingCard?.visibility = View.GONE
            binding?.next?.text = nextDate
            binding?.timeHome?.text = nextTime

        }
    }

    private fun GetTurnsData() {
        visitsArrayList = arrayListOf()
        println("$today==================Today")
        db = FirebaseFirestore.getInstance()
        db?.collection("visits")?.whereEqualTo("date", today)
            ?.orderBy("bookingtime", Query.Direction.ASCENDING)?.addSnapshotListener(


                object : EventListener<QuerySnapshot> {
                    @RequiresApi(Build.VERSION_CODES.O)
                    override fun onEvent(
                        value: QuerySnapshot?,
                        error: FirebaseFirestoreException?
                    ) {
                        if (error != null) {
                            println(error.message)
                            return
                        } else {
                            Thread(Runnable {
                                for (dc: DocumentChange in value!!.documentChanges) {
                                    if (dc.type == DocumentChange.Type.ADDED) {
                                        visitsArrayList?.add(dc.document.toObject(Visits::class.java))
                                    }
                                }
                                println("${visitsArrayList?.size}============user")
                                activity?.runOnUiThread { Filter() }


                            }).start()
                        }

                    }
                })

    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun Filter() {
        println("Filter==========================")
        var pendingList: ArrayList<Visits> = arrayListOf()
        var AllList: ArrayList<Visits> = arrayListOf()
        var cancelList: ArrayList<Visits> = arrayListOf()
        var completList: ArrayList<Visits> = arrayListOf()
        Thread(Runnable {
            for (visit: Visits in visitsArrayList!!) {
                var status = visit.status


                if (status == "Pending") {
                    pendingList.add(visit)

                }
                if (status == "completed") {
                    completList.add(visit)
                }


            }
            activity?.runOnUiThread {
                totalCompleteVisits = completList.size
                println("$totalCompleteVisits====================CCC")
                binding?.currentNumber?.text = totalCompleteVisits.toString()
                EstimatedTime()
                GetSettings()

            }
        }).start()


        println("${pendingList.size}===================Pending")


    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun EstimatedTime() {
        if (turn?.toInt()!! >= totalCompleteVisits!!) {


            val diff = turn!!.toInt() - totalCompleteVisits!!
            println("$waiting==============WAIT")
            val remaining = waiting!! * diff
            binding?.patientNumber?.text = diff.toString()


            val locale: Locale = Locale.US
            val sdf = DateTimeFormatter.ofPattern("hh:mm a").withLocale(locale)
            val time = LocalTime.now()
            val appointment = sdf.format(time.plusMinutes(remaining.toLong()))
            binding?.appointmentTime?.text = appointment


        } else {


        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun IsClinicOpen() {
        var open: String? = null
        var close: String? = null
        val locale = Locale.ENGLISH
        var available: Boolean? = null

        val sdf = DateTimeFormatter.ofPattern("hh:mm a", locale)
        var timeNow = LocalTime.now()
        db = FirebaseFirestore.getInstance()
        db?.collection("settings")?.document("settings")?.addSnapshotListener { value, error ->
            if (error != null) {
                println(error.message)
            } else {
                open = value!!.getString("open")!!
                close = value.getString("close")!!
                println("$close=============close")
                val openTime = LocalTime.parse(open!!.trim(), sdf)
                val closeTime = LocalTime.parse(close!!.trim(), sdf)
                available = timeNow > openTime && timeNow < closeTime

                println(available)
                if (available == true) {

                    Show_turn_Card()

                }


            }
        }


    }

    fun GetSettings() {
        db = FirebaseFirestore.getInstance()
        db?.collection("settings")?.document("settings")?.addSnapshotListener { value, error ->
            waiting = value?.getString("average")?.toInt()
        }
    }

}