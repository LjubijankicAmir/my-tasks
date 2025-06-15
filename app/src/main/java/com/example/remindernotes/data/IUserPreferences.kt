package com.example.remindernotes.data

interface IUserPreferences {
    fun setUserLoggedIn(isLoggedIn: Boolean)

    fun isUserLoggedIn(): Boolean
    fun setLoggedInUserId(userId: Int)
    fun getLoggedInUserId(): Int
    fun clear()
}