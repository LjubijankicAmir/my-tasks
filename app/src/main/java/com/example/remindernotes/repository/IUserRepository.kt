package com.example.remindernotes.repository

import com.example.remindernotes.data.User

interface IUserRepository {
    suspend fun register(user: User)

    suspend fun login(email: String, password: String): User?

    suspend fun getUserByEmail(email: String): User?

    suspend fun getAllUsers(): List<User>

    suspend fun getUserById(id: Int): User?
}