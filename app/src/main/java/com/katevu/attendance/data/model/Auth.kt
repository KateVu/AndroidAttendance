package com.katevu.attendance.data.model

import android.os.Build
import androidx.annotation.RequiresApi
import com.squareup.moshi.Json
import java.util.Calendar.getInstance

data class Auth @RequiresApi(Build.VERSION_CODES.O) constructor(
    @Json(name = "idToken")
    var _token: String?,
    @Json(name = "expiresIn")
    var expiresIn: String,
    @Json(name = "localId")
    var _userId: String?,
) {

    var expiredDate: Long? = null

    val token: String?
        @RequiresApi(Build.VERSION_CODES.O)
        get() {
            val cal = getInstance()
            if (expiredDate != null && (expiredDate!! > cal.timeInMillis) && _token != null) {
                return _token;
            }
            return null;
        }

    val userID: String?
        get() {
            return _userId;
        }

}


