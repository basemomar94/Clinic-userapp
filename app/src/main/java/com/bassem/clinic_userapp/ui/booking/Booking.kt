package com.bassem.clinic_userapp.ui.booking

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.navigation.NavDestination
import androidx.navigation.Navigation.findNavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment.findNavController
import androidx.navigation.fragment.findNavController
import com.bassem.clinic_userapp.R
import com.bassem.clinic_userapp.databinding.CalendarbookingFragmentBinding
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.reflect.Field
import java.sql.Time
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.*
import kotlin.collections.HashMap

class Booking : Fragment(R.layout.calendarbooking_fragment) {
    var _binding: CalendarbookingFragmentBinding? = null
    val binding get() = _binding
    var date: String? = null
    var db: FirebaseFirestore? = null
    var id: String? = null
    var visit: String? = null
    var name: String? = null
    var complain: String? = null
    var token: String? = null
    var turn: String? = null
    lateinit var estimatedTime: String
    var book = false
    var open: String? = null
    var waiting: Int? = null
    var holiDay: String? = null
    var maxPatiens: Int? = null
    var isFull: Boolean? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sharedPreferences = activity?.getSharedPreferences("PREF", Context.MODE_PRIVATE)
        id = sharedPreferences?.getString("id", "")
        name = sharedPreferences?.getString("name", "")
        complain = sharedPreferences?.getString("complain", "")
        token = sharedPreferences?.getString("token", token)
        GetSettings()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = CalendarbookingFragmentBinding.inflate(layoutInflater, container, false)
        return binding?.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        binding?.calendarView?.setOnDateChangeListener { calendarView, year, month, dayofMonth ->
            var realmonth: Int = month + 1
            date = "$dayofMonth-$realmonth-$year"
            IsDayFull(date!!)

        }
        binding?.confirmBu?.setOnClickListener {
            book = true
            binding!!.confirmBu.text = ""
            binding!!.loading.visibility = View.VISIBLE
            binding!!.confirm.isClickable = false
            confirmBu.alpha = .5F
            try {
                Book()

            } catch (E: Exception) {
                println(E.message)
                binding!!.confirmBu.text = "Confirm"
                binding!!.loading.visibility = View.INVISIBLE
                binding!!.confirm.isClickable = true
                confirmBu.alpha = 1F

            }


        }
    }

    override fun onDestroy() {
        super.onDestroy()
        activity?.supportFragmentManager?.isDestroyed

    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun Book() {

        db = FirebaseFirestore.getInstance()
        var note: String = binding?.note?.text.toString()
        var data = HashMap<String, Any>()
        data.put("date", date!!)
        data.put("note", note)
        data.put("id", id!!)
        data.put("status", "Pending")
        data.put("Booked_by", "You")
        data.put("name", name!!)
        data.put("complain", complain!!)
        data.put("token", token!!)
        data["visit_time"] = estimatedTime
        data.put("bookingtime", FieldValue.serverTimestamp())
        db?.collection("visits")?.add(data)?.addOnCompleteListener {
            if (it.isSuccessful) {
                visit = it.result?.id

                VisitTurn()
            }

        }


    }

    fun HasVisit() {
        db = FirebaseFirestore.getInstance()
        var update = HashMap<String, Any>()
        update["next_visit"] = date!!
        update["IsVisit"] = true
        update["visit_id"] = visit!!
        update["turn"] = turn!!
        update["visit_time"] = estimatedTime!!
        db!!.collection("patiens_info").document(id!!).update(update)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    db!!.collection("visits").document(visit!!).update("visit", visit)

                   findNavController().navigateUp()


                }


            }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun VisitTurn() {
        db = FirebaseFirestore.getInstance()
        db!!.collection("visits").whereEqualTo("date", date).whereEqualTo("status", "Pending").get()
            .addOnCompleteListener {
                turn = it.result?.size().toString()
                val locale = Locale.ENGLISH
                if (!book) {
                    val sdf = DateTimeFormatter.ofPattern("hh:mm a", locale)
                    //Booking on the same day problem
                    val dateNow = LocalDate.now()
                    val locale = Locale.US
                    val sdate = DateTimeFormatter.ofPattern("d-M-yyyy", locale)
                    val visitDate = LocalDate.parse(date, sdate)
                    var workTime: LocalTime = if (visitDate == dateNow) {
                        var timeNow = sdf.format(LocalTime.now())
                        LocalTime.parse(timeNow.toString(), sdf)

                    } else {
                        LocalTime.parse(open, sdf)
                    }
                    val waitingTime = waiting!! * turn!!.toInt()
                    estimatedTime = sdf.format(workTime.plusMinutes(waitingTime.toLong()))
                    binding?.time!!.text = estimatedTime
                }

                if (book) {
                    HasVisit()
                }


            }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun ValidBooking(date: String): Boolean {
        val locale = Locale.ENGLISH
        val sdf = DateTimeFormatter.ofPattern("d-M-yyyy", locale)
        val visitDate: LocalDate = LocalDate.parse(date, sdf)
        val dateNow = LocalDate.now()
        return visitDate > dateNow
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun AfterDays(date: String): String {
        var result: String
        val dateNow = LocalDate.now()
        val locale = Locale.US
        val sdf = DateTimeFormatter.ofPattern("d-M-yyyy", locale)
        val visitDate = LocalDate.parse(date, sdf)
        result = if (visitDate == dateNow) {
            "Your visit will be today"
        } else {
            var differnt = ChronoUnit.DAYS.between(dateNow, visitDate)
            "Your visit will be after $differnt days"
        }
        return result

    }

    fun GetSettings() {
        db = FirebaseFirestore.getInstance()
        db!!.collection("settings").document("settings").addSnapshotListener { value, error ->
            if (error != null) {
                println(error.message)
            } else {
                open = value?.getString("open")
                waiting = value!!.getString("average")?.toInt()
                holiDay = value.getString("holiday")?.trim()
                maxPatiens = value.getString("max")?.trim()?.toInt()


            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun IsHoliday(): Boolean {
        var holiday: Boolean
        val cal = Calendar.getInstance()
        val locale = Locale.US
        val sdf = SimpleDateFormat("d-m-yyyy", locale)
        val calDate: Date = sdf.parse(date)
        cal.time = calDate
        var dayNumber: Int = cal.get(Calendar.DAY_OF_WEEK)
        var daysList = listOf<String>(
            "SUNDAY",
            "MONDAY",
            "TUESDAY",
            "WEDNESDAY",
            "THURSDAY",
            "FRIDAY",
            "SATURDAY"
        )
        var dayName = daysList[dayNumber - 1]
        holiday = dayName == holiDay

        return holiday
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun IsDayFull(date: String) {
        var currentPatients: Int? = null
        db = FirebaseFirestore.getInstance()
        db!!.collection("visits").whereEqualTo("date", date).whereEqualTo("status", "Pending").get()
            .addOnCompleteListener {
                currentPatients = it.result?.size()
                println(currentPatients)
                println(maxPatiens)
                isFull = maxPatiens!! <= currentPatients!!
                if (isFull!!) {
                    BookingUnavaiable(
                        "We are sorry, we have reached maximum patients for these day, check another date"
                    )

                } else {
                    if (ValidBooking(date)) {
                        if (IsHoliday()) {
                            BookingUnavaiable("We are sorry it is our holiday")

                        } else {
                            BookingAvailable()
                        }

                    } else {
                        BookingUnavaiable("the visit should be in the future")
                    }

                }
            }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun BookingAvailable() {
        binding?.nextvisit?.setTextColor(Color.GREEN)
        binding?.nextvisit?.text = AfterDays(date!!)
        binding?.card?.visibility = View.VISIBLE
        binding?.confirm?.visibility = View.VISIBLE
        binding?.note?.visibility = View.VISIBLE
        binding?.time?.visibility = View.VISIBLE
        binding?.time2?.visibility = View.VISIBLE
        binding?.textView9?.visibility = View.VISIBLE
        VisitTurn()
    }

    fun BookingUnavaiable(errorMessage: String) {
        binding?.nextvisit?.setTextColor(Color.RED)
        binding?.card?.visibility = View.VISIBLE
        binding?.confirm?.visibility = View.GONE
        binding?.note?.visibility = View.GONE
        binding?.time?.visibility = View.GONE
        binding?.time2?.visibility = View.GONE
        binding?.textView9?.visibility = View.GONE
        binding?.nextvisit?.text = errorMessage


    }


}