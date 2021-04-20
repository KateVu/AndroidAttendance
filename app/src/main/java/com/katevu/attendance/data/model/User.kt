package com.katevu.attendance.data.model


data class User (
//    @Json(name = "email")
    var email: String,
//    @Json(name = "password")
    var password: String,
//    @Json(name = "returnSecureToken")
    var returnSecureToken: Boolean,
)