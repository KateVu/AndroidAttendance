package com.katevu.attendance.ui.login

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.katevu.attendance.R
import com.katevu.attendance.models.Auth
import com.katevu.attendance.models.User
import com.katevu.attendance.network.LoginApi
import kotlinx.coroutines.launch
import java.util.*

class LoginViewModel() : ViewModel() {

    private val _loginForm = MutableLiveData<LoginFormState>()
    val loginFormState: LiveData<LoginFormState> = _loginForm

    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult
    private val _auth = MutableLiveData<Auth>()

    // The external LiveData interface to the property is immutable, so only this class can modify
    val auth: LiveData<Auth> = _auth


    fun getPost(email: String, password: String){
        viewModelScope.launch {
            val user = User(email, password, true)
            val response = LoginApi.retrofitService.login(user)
            val result = response.body()

            val cal: Calendar = Calendar.getInstance();
            cal.timeInMillis

            if (result != null) {
                result.expiredDate = cal.timeInMillis + (result.expiresIn.toLong() * 10000)
            }
            _auth.value = result!!
        }
    }

    fun login(username: String, password: String) {
        // can be launched in a separate asynchronous job

        viewModelScope.launch {
            val user = User(username, password, true)
            val response = LoginApi.retrofitService.login(user)
            val result = response.body()

            val cal: Calendar = Calendar.getInstance();
            cal.timeInMillis

            if (result != null) {
                result.expiredDate = cal.timeInMillis + (result.expiresIn.toLong() * 10000)
            }
            _auth.value = result!!
        }
    }

    fun loginDataChanged(username: String, password: String) {
        if (!isUserNameValid(username)) {
            _loginForm.value = LoginFormState(usernameError = R.string.invalid_username)
        } else if (!isPasswordValid(password)) {
            _loginForm.value = LoginFormState(passwordError = R.string.invalid_password)
        } else {
            _loginForm.value = LoginFormState(isDataValid = true)
        }
    }

    // A placeholder username validation check
    private fun isUserNameValid(username: String): Boolean {
        return if (username.contains("@")) {
            Patterns.EMAIL_ADDRESS.matcher(username).matches()
        } else {
            username.isNotBlank()
        }
    }

    // A placeholder password validation check
    private fun isPasswordValid(password: String): Boolean {
        return password.length > 1
    }
}