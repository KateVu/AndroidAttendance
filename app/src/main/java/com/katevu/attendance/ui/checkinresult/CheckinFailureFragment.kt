package com.katevu.attendance.ui.checkinresult

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.katevu.attendance.databinding.FragmentCheckinFailureBinding

private const val ERROR_MESSAGE = "error message"

class CheckinFailureFragment : Fragment() {

    private lateinit var binding: FragmentCheckinFailureBinding

    private var errorMessage: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            errorMessage = it.getString(ERROR_MESSAGE)
        }

    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCheckinFailureBinding.inflate(inflater, container, false);

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.errormessage.text = errorMessage
    }

    companion object {
        @JvmStatic
        fun newInstance(errorMessage: String) =
            CheckinFailureFragment().apply {
                arguments = Bundle().apply {
                    putString(ERROR_MESSAGE, errorMessage)
                }
            }
    }



}