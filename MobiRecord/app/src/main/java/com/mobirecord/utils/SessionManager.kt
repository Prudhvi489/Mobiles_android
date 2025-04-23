package com.mobirecord.utils

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {

    var context: Context? = context

    companion object {
        lateinit var sharedPreferences: SharedPreferences
        lateinit var editor: SharedPreferences.Editor
        val PREFER_NAME = "TuneConnect"
        var PRIVATE_MODE = 0
        const val IS_USER_LOGIN = "IsUserLoggedIn"
    }

    init {
        sharedPreferences = context.getSharedPreferences(PREFER_NAME, PRIVATE_MODE)
        editor = sharedPreferences.edit()
        editor.commit()
    }

    fun <T> saveData(name: String, value: T) = with(sharedPreferences?.edit()) {
        when (value) {
            is Long -> this?.putLong(name, value)?.apply()
            is String -> this?.putString(name, value)?.apply()
            is Int -> this?.putInt(name, value)?.apply()
            is Boolean -> this?.putBoolean(name, value)?.apply()
            is Float -> this?.putFloat(name, value)?.apply()
            else -> throw IllegalArgumentException("This type can be saved into Preferences")
        }

    }

    fun <T> getData(name: String, default: T): T = with(sharedPreferences) {
        val res: Any = when (default) {
            is Long -> this?.getLong(name, default)
            is String -> this?.getString(name, default)
            is Int -> this?.getInt(name, default)
            is Boolean -> this?.getBoolean(name, default)
            is Float -> this?.getFloat(name, default)
            else -> throw IllegalArgumentException("This type can't be saved into Preferences")
        } ?: ""

        res as T
    }

    fun isUserLoggedIn(): Boolean {
        return sharedPreferences.getBoolean(IS_USER_LOGIN, false)
    }

    fun createUserLoginSession() {
        editor.putBoolean(IS_USER_LOGIN, true)
        editor.commit()
    }
    fun clearSession() {
        editor.clear()
        editor.commit()
    }
}