package com.katevu.attendance.ui.checkinresult

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.katevu.attendance.databinding.FragmentCheckinSuccessBinding

private const val SUCCESS_LOCATION = "location"
private const val SUCCESS_DATE = "date"

class CheckinSuccessFragment : Fragment() {

    private lateinit var binding: FragmentCheckinSuccessBinding

    private var location: String? = null
    private var date: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            location = it.getString(SUCCESS_LOCATION)
            date = it.getString(SUCCESS_DATE)
        }

    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCheckinSuccessBinding.inflate(inflater, container, false);

        //here data must be an instance of the class MarsDataProvider
        //here data must be an instance of the class MarsDataProvider
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.location.text = location
        binding.date.text = date
    }

    companion object {
        @JvmStatic
        fun newInstance(location: String, date: String) =
            CheckinSuccessFragment().apply {
                arguments = Bundle().apply {
                    putString(SUCCESS_LOCATION, location)
                    putString(SUCCESS_DATE, date)
                }
            }
    }



}