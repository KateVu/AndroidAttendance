package com.katevu.attendance

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.katevu.attendance.data.model.Attendance
import com.katevu.attendance.network.SubmitAtApi
import kotlinx.coroutines.launch

class CheckinActivityViewModel: ViewModel() {


    private val _checkinResult = MutableLiveData<Boolean>()
    val checkinResult: LiveData<Boolean> = _checkinResult


    fun checkin(url: String, attendance: Attendance) {
        // can be launched in a separate asynchronous job

        viewModelScope.launch {
            val response = SubmitAtApi.retrofitService.submitAttendance(url, attendance)

            val responseCode = response.code()

            _checkinResult.value = responseCode == 200

        }
    }

}