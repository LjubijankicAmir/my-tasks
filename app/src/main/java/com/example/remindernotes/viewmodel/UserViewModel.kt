package com.example.remindernotes.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.remindernotes.data.IUserPreferences
import com.example.remindernotes.data.User
import com.example.remindernotes.repository.IUserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UserViewModel(private val repository: IUserRepository, private val userPreferences: IUserPreferences) : ViewModel() {

    private val _loggedInUser = MutableStateFlow<User?>(null)
    val loggedInUser: StateFlow<User?> get() = _loggedInUser

    private val _loginStatusMessage = MutableStateFlow("")
    val loginStatusMessage: StateFlow<String> get() = _loginStatusMessage
    fun resetLoginStatusMessage() {
        _loginStatusMessage.value = ""
    }

    init {
        viewModelScope.launch {
            _loggedInUser.value = getLoggedInUser()
        }
    }
    fun isLoggedIn(): Boolean {
        return userPreferences.isUserLoggedIn()
    }

    fun setLoggedIn(isLoggedIn: Boolean) {
        userPreferences.setUserLoggedIn(isLoggedIn)
    }

    suspend fun userExists(email: String): Boolean {
        return repository.getUserByEmail(email) != null
    }

    suspend fun register(user: User) {
        if (!userExists(user.email)) {
            repository.register(user)
        } else {
            throw IllegalArgumentException("User already exists")
        }
    }

    suspend fun login(email: String, password: String): User? {
        val user = repository.login(email, password)
        try {
            if (user != null) {
                setLoggedIn(true)
                userPreferences.setLoggedInUserId(user.id)
                _loggedInUser.value = user
                return user
            } else {
                throw IllegalArgumentException("Invalid email or password")
            }
        } catch(it: Exception) {
            _loginStatusMessage.value = "Invalid email or password"
            return null
        }
    }
    fun logout() {
        _loggedInUser.value = null
        userPreferences.clear()
    }

    fun setLoggedInUserId(userId: Int) {
        userPreferences.setLoggedInUserId(userId)
    }
    suspend fun getLoggedInUser(): User? {
        val userId = userPreferences.getLoggedInUserId()
        return if (userId != -1) repository.getUserById(userId) else null
    }

    public override fun onCleared() {
        super.onCleared()
    }
}