package com.example.remindernotes.data

import android.content.Context
import android.content.SharedPreferences

class UserPreferences(context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("userPreferences", Context.MODE_PRIVATE)

    fun setUserLoggedIn(isLoggedIn: Boolean) {
        sharedPreferences.edit().putBoolean("isLoggedIn", isLoggedIn).apply()
    }

    fun isUserLoggedIn(): Boolean {
        return sharedPreferences.getBoolean("isLoggedIn", false)
    }
    fun setLoggedInUserId(userId: Int) {
        sharedPreferences.edit().putInt("userId", userId).apply()
    }
    fun getLoggedInUserId(): Int {
        return sharedPreferences.getInt("userId", -1)
    }
    fun clear() {
        sharedPreferences.edit().clear().apply()
    }
}