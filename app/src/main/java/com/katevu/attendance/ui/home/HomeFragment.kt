package com.katevu.attendance.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.katevu.attendance.R
import com.katevu.attendance.ui.checkinresult.CheckinFailureFragment
import com.katevu.attendance.ui.checkinresult.CheckinSuccessFragment


class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        homeViewModel.text.observe(viewLifecycleOwner, Observer {
        })
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        insertSuccessFragment()
    }

    // Embeds the child fragment dynamically
    private fun insertSuccessFragment() {
        val childFragment = CheckinSuccessFragment.newInstance("You are in room: BA101", "14.00 PM 23 Apr, 2021")
        val transaction: FragmentTransaction = childFragmentManager.beginTransaction()
        transaction.replace(R.id.child_fragment_container, childFragment).commit()
    }

    private fun insertFailureFragment() {
        val childFragment = CheckinFailureFragment.newInstance("Cannot connect. Please try again")
        val transaction: FragmentTransaction = childFragmentManager.beginTransaction()
        transaction.replace(R.id.child_fragment_container, childFragment).commit()
    }

}