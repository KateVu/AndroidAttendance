package com.katevu.attendance.data.model

data class Attendance(
        var _token: String,
        var _userId: String?,
        var _nfcId: String,
        var _date: String,
)
