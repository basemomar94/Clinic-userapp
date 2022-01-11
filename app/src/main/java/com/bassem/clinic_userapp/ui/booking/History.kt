package com.bassem.clinic_userapp.ui.booking

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bassem.clinic_userapp.R
import com.bassem.clinic_userapp.databinding.HistoryFragmentBinding
import com.google.firebase.firestore.*

class History() : Fragment(R.layout.history_fragment), HistoryAdapter.Myclicklisener {
    var _binding: HistoryFragmentBinding? = null
    val binding get() = _binding
    lateinit var recyclerView: RecyclerView
    lateinit var adapter: HistoryAdapter
    lateinit var visitsArrayList: ArrayList<Visits>
    lateinit var id: String
    lateinit var db: FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var sharedPreferences = activity?.getSharedPreferences("PREF", Context.MODE_PRIVATE)
        id = sharedPreferences?.getString("id", "")!!
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = HistoryFragmentBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onResume() {
        super.onResume()
        adapter.notifyDataSetChanged()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        visitsArrayList = arrayListOf()
        Recycle_Setup(visitsArrayList)

        EventChangeListner()
    }

    fun EventChangeListner() {
        db = FirebaseFirestore.getInstance()
        db.collection("visits").whereEqualTo("id", id)
            .orderBy("bookingtime", Query.Direction.DESCENDING).addSnapshotListener(
                object : EventListener<QuerySnapshot> {
                    override fun onEvent(
                        value: QuerySnapshot?,
                        error: FirebaseFirestoreException?
                    ) {
                        if (error != null) {
                            println(error.message)
                        } else {
                            Thread(Runnable {
                                for (dc: DocumentChange in value!!.documentChanges) {
                                    if (dc.type == DocumentChange.Type.ADDED) {
                                        visitsArrayList.add(dc.document.toObject(Visits::class.java))

                                    }

                                }
                                activity?.runOnUiThread {
                                    adapter.notifyDataSetChanged()
                                }
                            }).start()


                        }
                    }

                }
            )


    }

    override fun onClick(position: Int) {
        val visit = visitsArrayList[position].visit
        val navController = Navigation.findNavController(activity!!, R.id.nav_host_fragment)
        var bundle = Bundle()
        bundle.putString("visit", visit)
        navController.navigate(R.id.action_booking_to_visitsExpand, bundle)
    }
    fun Recycle_Setup(list: ArrayList<Visits>){
        recyclerView = view!!.findViewById(R.id.historyRV)
        adapter = HistoryAdapter(list, this)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter
    }

}


