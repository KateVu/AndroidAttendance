package com.katevu.attendance.ui.login

import com.katevu.attendance.data.model.Auth

/**
 * Authentication result : success (user details) or error message.
 */
data class LoginResult(
    val success: Auth? = null,
    val error: Int? = null
)