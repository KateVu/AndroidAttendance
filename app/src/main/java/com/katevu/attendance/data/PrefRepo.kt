package com.katevu.attendance.data

import PREFERENCE_NAME
import PREF_LOGGED_IN
import PREF_LOGGED_USER
import PREF_SHARE_MESSAGE
import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.katevu.attendance.data.model.Auth


class PrefRepo(val context: Context) {


    private val pref: SharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)
    private val editor = pref.edit()
    private val gson = Gson()

    private fun String.put(long: Long) {
        editor.putLong(this, long)
        editor.commit()
    }

    private fun String.put(int: Int) {
        editor.putInt(this, int)
        editor.commit()
    }

    private fun String.put(string: String) {
        editor.putString(this, string)
        editor.commit()
    }

    private fun String.put(boolean: Boolean) {
        editor.putBoolean(this, boolean)
        editor.commit()
    }


    private fun String.getLong() = pref.getLong(this, 0)

    private fun String.getInt() = pref.getInt(this, 0)

    private fun String.getString() = pref.getString(this, "")!!

    private fun String.getBoolean() = pref.getBoolean(this, false)

    fun setLoggedIn(isLoggedIn: Boolean) {
        PREF_LOGGED_IN.put(isLoggedIn)
    }

    fun getLoggedIn() = PREF_LOGGED_IN.getBoolean()

    fun setShareMsg(msg: String) {
        PREF_SHARE_MESSAGE.put(msg)
    }

    fun getShareMsg() = PREF_SHARE_MESSAGE.getString()

    fun clearData() {
        editor.clear()
        editor.commit()
    }


    fun setLogginUser(user: Auth) {
        var jsonString: String = gson.toJson(user);
        PREF_LOGGED_USER.put(jsonString);
    }

    fun getLogginUser() = gson.fromJson<Auth>(PREF_LOGGED_USER.getString(), Auth::class.java)

}
