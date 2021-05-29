package com.katevu.attendance.ui.checkinresult

data class CheckinResult(
    val success: InfoCheckin? = null,
    val error: Int? = null
)

data class InfoCheckin(
    val location: String,
    val date: String,
)

