package com.katevu.attendance.data.model

import com.squareup.moshi.Json

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
data class LoggedInUser(
        val message: String,
        val data: UserData,
        val token: String,
)

data class UserData(
        val isEmailVerified: Boolean,
        val units: List<String>,
        @Json(name = "_id")
        val id: String,
        val studentID: String,
)

data class Unit(
        val id: String,
        val name: String,
)
