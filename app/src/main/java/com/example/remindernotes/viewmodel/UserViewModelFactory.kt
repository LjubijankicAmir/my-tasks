package com.example.remindernotes.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.remindernotes.data.UserDao
import com.example.remindernotes.data.UserPreferences
import com.example.remindernotes.repository.UserRepository

class UserViewModelFactory(private val userPreferences: UserPreferences) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UserViewModel(UserRepository(), userPreferences) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}