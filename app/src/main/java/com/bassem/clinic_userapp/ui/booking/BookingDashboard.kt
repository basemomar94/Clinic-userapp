package com.bassem.clinic_userapp.ui.booking

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.bassem.clinic_userapp.R
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator


class BookingDashboard() : Fragment(R.layout.booking_dashboard) {

    var tabtitle = arrayOf("Upcoming", "History")
    lateinit var pager2: ViewPager2
    lateinit var tabl:TabLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
         pager2 = view.findViewById<ViewPager2>(R.id.ViewPager)
         tabl = view.findViewById<TabLayout>(R.id.tablayout)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        pager2.adapter=PageViewerAdapter(activity!!.supportFragmentManager,activity!!.lifecycle)

        TabLayoutMediator(tabl, pager2) {

                tab, position ->
            tab.text = tabtitle[position]

        }.attach()
    }


}





