package com.katevu.attendance.ui.login

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.katevu.attendance.R
import com.katevu.attendance.data.PrefRepo
import com.katevu.attendance.data.model.LoggedInUser
import com.katevu.attendance.databinding.FragmentLoginBinding
import java.util.*


class LoginFragment : Fragment() {

    private val TAG = "LoginFragment"

    interface Callbacks {
        fun loginSuccessful()
    }

    lateinit var binding: FragmentLoginBinding
    private val loginViewModel: LoginViewModel by viewModels()
    private var callbacks: Callbacks? = null
    private val prefRepository by lazy { PrefRepo(requireContext()) }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        callbacks = context as Callbacks?
    }

    override fun onDetach() {
        super.onDetach()
        callbacks = null
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?,
    ): View? {

        binding = FragmentLoginBinding.inflate(inflater, container, false);
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val cm = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
        val isConnected: Boolean = activeNetwork?.isConnectedOrConnecting == true


        val logginUser = prefRepository.getLogginUser();

        if (logginUser != null) {
//            val cal: Calendar = Calendar.getInstance();
//            val isValid = if (cal.timeInMillis < logginUser.expiredDate!!) {true} else false
            val isValid = true;
            if (isValid) {
                callbacks?.loginSuccessful()
            } else {
                prefRepository.clearData()
            }
        }

        loginViewModel.loginFormState.observe(viewLifecycleOwner,
                Observer { loginFormState ->
                    if (loginFormState == null) {
                        return@Observer
                    }
                    binding.login.isEnabled = loginFormState.isDataValid
                    loginFormState.usernameError?.let {
                        binding.username.error = getString(it)
                    }
                    loginFormState.passwordError?.let {
                        binding.password.error = getString(it)
                    }
                })

        loginViewModel.loginResult.observe(viewLifecycleOwner,
                Observer { loginResult ->
                    loginResult ?: return@Observer
                    binding.loading.visibility = View.GONE
                    loginResult.error?.let {
                        showLoginFailed(it.toString())
                    }
                    loginResult.success?.let {
                        //To set the value
//                    prefRepository.setLoggedIn(true)
                        prefRepository.setLogginUser(it)
                        updateUiWithUser(it)
                    }
                })

        if (isConnected) {
            Log.d(TAG, "network work");
        } else {
            Log.d(TAG, "network does not work");
        }

        val afterTextChangedListener = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // ignore
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // ignore
            }

            override fun afterTextChanged(s: Editable) {
                loginViewModel.loginDataChanged(
                        binding.username.text.toString(),
                        binding.password.text.toString()
                )
            }
        }
        binding.username.addTextChangedListener(afterTextChangedListener)
        binding.password.addTextChangedListener(afterTextChangedListener)
        binding.password.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                loginViewModel.login(
                        binding.username.text.toString(),
                        binding.password.text.toString())

            }
            false
        }

        binding.login.setOnClickListener {

            if (!isConnected) {
                showLoginFailed("There is no internet access!!!!")
            } else {
                binding.loading.visibility = View.VISIBLE
                loginViewModel.login(
                        binding.username.text.toString(),
                        binding.password.text.toString()
                )
            }
        }
    }

    private fun updateUiWithUser(model: LoggedInUser) {
        val welcome = getString(R.string.welcome) + model.data.studentID
        val appContext = context?.applicationContext ?: return
        Toast.makeText(appContext, welcome, Toast.LENGTH_LONG).show()
        callbacks?.loginSuccessful()
    }

    private fun showLoginFailed(errorString: String) {
        val appContext = context?.applicationContext ?: return
        Toast.makeText(appContext, errorString, Toast.LENGTH_LONG).show()
    }


    companion object {
        fun newInstance(): LoginFragment {
            return LoginFragment()
        }
    }

}