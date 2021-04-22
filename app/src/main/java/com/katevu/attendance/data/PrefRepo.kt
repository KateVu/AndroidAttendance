package com.katevu.attendance.data

import PREFERENCE_NAME
import PREF_LOGGED_IN
import PREF_SHARE_MESSAGE
import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson


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

//    MyObject myObject = new MyObject;
////set variables of 'myObject', etc.
//
//    Editor prefsEditor = mPrefs.edit();
//    Gson gson = new Gson();
//    String json = gson.toJson(myObject);
//    prefsEditor.putString("MyObject", json);
//    prefsEditor.commit();
//    To retrieve:
//
//    Gson gson = new Gson();
//    String json = mPrefs.getString("MyObject", "");
//    MyObject obj = gson.fromJson(json, MyObject.class);
}
