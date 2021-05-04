package com.katevu.attendance.ui.classes

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.katevu.attendance.data.model.MyClass

class TodayClassViewModel: ViewModel() {
    private val TAG = "TodayClassViewModel"


    private var _todayclasses = MutableLiveData<List<MyClass>>()

    val todayclasses: LiveData<List<MyClass>>
        get() = _todayclasses

    fun getClasses() {
        _todayclasses.value = arrayListOf(
                MyClass("12:30PM - 2:30PM", "ICT90004-Applied Research Project"),
                MyClass("3:30PM - 5:30PM", "ICT90004-Applied Research Method"),
        )
    }
}