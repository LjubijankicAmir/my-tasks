package com.example.remindernotes.data

import android.content.Context
import android.content.SharedPreferences

class UserPreferences(context: Context) : IUserPreferences {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("userPreferences", Context.MODE_PRIVATE)

    override fun setUserLoggedIn(isLoggedIn: Boolean) {
        sharedPreferences.edit().putBoolean("isLoggedIn", isLoggedIn).apply()
    }

    override fun isUserLoggedIn(): Boolean {
        return sharedPreferences.getBoolean("isLoggedIn", false)
    }
    override fun setLoggedInUserId(userId: Int) {
        sharedPreferences.edit().putInt("userId", userId).apply()
    }
    override fun getLoggedInUserId(): Int {
        return sharedPreferences.getInt("userId", -1)
    }
    override fun clear() {
        sharedPreferences.edit().clear().apply()
    }
}