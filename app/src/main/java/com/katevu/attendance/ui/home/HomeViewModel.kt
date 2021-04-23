package com.katevu.attendance.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HomeViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "If there is a class in range and have not check in/out yet display: Please tab to submit your attention"
    }
    val text: LiveData<String> = _text
}