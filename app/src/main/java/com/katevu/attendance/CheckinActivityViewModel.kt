package com.katevu.attendance

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.katevu.attendance.data.model.Attendance
import com.katevu.attendance.data.model.GetActivitiesResult
import com.katevu.attendance.data.model.StudentActivity
import com.katevu.attendance.network.GetActivitiesApi
import com.katevu.attendance.network.SubmitAtApi
import com.katevu.attendance.ui.checkinresult.CheckinResult
import com.katevu.attendance.ui.checkinresult.InfoCheckin
import kotlinx.coroutines.launch

private const val ACTIVITIES_URL = "https://mobile-attendance-recorder.herokuapp.com/api/v1/activities"

class CheckinActivityViewModel: ViewModel() {

    private val TAG = "CheckinActivityViewModel"

//    private val _checkinResult = MutableLiveData<Boolean>()
//    val checkinResult: LiveData<Boolean> = _checkinResult

    private val _checkinResult1 = MutableLiveData<CheckinResult>()
    val checkinResult1: LiveData<CheckinResult> = _checkinResult1

    private val _getActivitiesResult = MutableLiveData<GetActivitiesResult>()
    val getActivitiesResult: LiveData<GetActivitiesResult> = _getActivitiesResult
    private  var _listActivites: List<StudentActivity>? = mutableListOf()

    var result: Boolean = false;


    fun getActivities() {
        Log.d(TAG, "Call get activities")

        // can be launched in a separate asynchronous job

        viewModelScope.launch {
            val response = GetActivitiesApi.retrofitService.getActivites(ACTIVITIES_URL)

            val responseCode = response.code()

//            _checkinResult.value = responseCode == 200

            result = responseCode == 200


            if (responseCode != 200) {
                _getActivitiesResult.value =  GetActivitiesResult(null, responseCode)
            } else {
                val result = response.body();
//
//                val cal: Calendar = Calendar.getInstance();
//                cal.timeInMillis

                _getActivitiesResult.value = GetActivitiesResult(result, null)
                _listActivites = result?.data

                Log.d(TAG, "result: ${responseCode}")

                Log.d(TAG, "result: ${result}")
            }

            Log.d(TAG, "response code: ${response.code()}")

            Log.d(TAG, "result: ${result}")

        }
    }

    init {
        getActivities()
    }

    fun checkin(url: String, token: String, attendance: Attendance) {

        Log.d(TAG, "Call check in with url: $url");
        Log.d(TAG, "Call check in with token: $token")
        Log.d(TAG, "Call check in with datetime: ${attendance.dateTime}")

        // can be launched in a separate asynchronous job

        viewModelScope.launch {
            val response = SubmitAtApi.retrofitService.submitAttendance(url, token, attendance)

            val responseCode = response.code()

//            _checkinResult.value = responseCode == 200

            result = responseCode == 200


            if (responseCode != 200) {
                _checkinResult1.value =  CheckinResult(null, responseCode)
            } else {
                val result = InfoCheckin(
                    attendance.nfcID,
                    attendance.dateTime
                )
//
//                val cal: Calendar = Calendar.getInstance();
//                cal.timeInMillis

                _checkinResult1.value = CheckinResult(result, null)

                Log.d(TAG, "result: ${responseCode}")

                Log.d(TAG, "result: ${result}")
            }

            Log.d(TAG, "response code: ${response.code()}")

            Log.d(TAG, "result: ${result}")

        }
    }

}