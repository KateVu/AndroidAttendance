package com.katevu.attendance.data.model

data class Attendance(
        var token: String,
        var userId: String?,
        var nfcId: String,
        var date: String,
)
