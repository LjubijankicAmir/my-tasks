package com.example.remindernotes.repository

import com.example.remindernotes.data.Task

interface ITaskRepository {
    suspend fun getAll(): List<Task>
    suspend fun getTaskById(id: String): Task?
    suspend fun insertTask(task: Task): Task
    suspend fun updateTask(task: Task)
    suspend fun deleteTask(task: Task)
    suspend fun getTasksForUser(userId: Int): List<Task>
}
