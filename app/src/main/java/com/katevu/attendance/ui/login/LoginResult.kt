package com.katevu.attendance.ui.login

import com.katevu.attendance.data.model.LoggedInUser

/**
 * Authentication result : success (user details) or error message.
 */
data class LoginResult(
    val success: LoggedInUser? = null,
    val error: Int? = null
)