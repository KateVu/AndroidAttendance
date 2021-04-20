package com.katevu.attendance.models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.katevu.attendance.network.LoginApi
import kotlinx.coroutines.launch
import java.util.*


enum class LoginApiStatus { LOADING, ERROR, DONE }

class LoginViewModel1 : ViewModel() {
    private val TAG = "LoginViewModel"

    // The internal MutableLiveData that stores the status of the most recent request
    private val _status = MutableLiveData<LoginApiStatus>()

    // The external immutable LiveData for the request status
    val status: LiveData<LoginApiStatus> = _status

    // Internally, we use a MutableLiveData, because we will be updating the List of MarsPhoto
    // with new values
    private val _auth = MutableLiveData<Auth>()

    // The external LiveData interface to the property is immutable, so only this class can modify
    val auth: LiveData<Auth> = _auth


    private  val _resutl = MutableLiveData<String>()
    val result: LiveData<String> = _resutl

    /**
     * Call getMarsPhotos() on init so we can display status immediately.
     */
    init {
//        getMarsPhotos()
    }

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

}

