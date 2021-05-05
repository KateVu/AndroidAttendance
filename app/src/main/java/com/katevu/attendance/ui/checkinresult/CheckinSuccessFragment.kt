package com.katevu.attendance.ui.checkinresult

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.katevu.attendance.databinding.FragmentCheckinSuccessBinding

private const val SUCCESS_LOCATION = "location"
private const val SUCCESS_DATE = "date"

class CheckinSuccessFragment : Fragment() {

    interface Callbacks {
        fun dismissSuccessMessage()
    }

    private lateinit var binding: FragmentCheckinSuccessBinding
    private var callbacks: Callbacks? = null

    private var location: String? = null
    private var date: String? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callbacks = context as Callbacks
    }

    override fun onDetach() {
        super.onDetach()
        callbacks = null
    }

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

        binding.buttonSuccess.setOnClickListener{
            callbacks?.dismissSuccessMessage()
        }
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