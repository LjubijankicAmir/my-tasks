package com.example.remindernotes.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks")
    suspend fun getAll(): List<Task>

    @Insert
    suspend fun insertTask(task: Task)
    @Delete
    suspend fun deleteTask(task: Task)
    @Update
    suspend fun updateTask(task: Task)
    @Query("SELECT * FROM tasks WHERE userId = :userId")
    suspend fun getTasksForUser(userId: Int): List<Task>
}