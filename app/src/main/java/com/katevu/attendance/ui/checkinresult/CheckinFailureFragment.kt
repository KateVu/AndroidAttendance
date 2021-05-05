package com.katevu.attendance.ui.checkinresult

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.katevu.attendance.databinding.FragmentCheckinFailureBinding

private const val ERROR_MESSAGE = "error message"

class CheckinFailureFragment : Fragment() {

    private var TAG = "CheckinFailureFragment"

    interface Callbacks {
        fun dismissFailureMessage()
    }

    private lateinit var binding: FragmentCheckinFailureBinding
    private var callbacks: Callbacks? = null

    private var errorMessage: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            errorMessage = it.getString(ERROR_MESSAGE)
        }

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callbacks = context as Callbacks
    }

    override fun onDetach() {
        super.onDetach()
        callbacks = null
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

        binding.buttonFailure.setOnClickListener {
            Log.d(TAG, "Click on OK button")
            callbacks?.dismissFailureMessage()
        }
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