package com.example.remindernotes.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.LocalTime

@Entity(tableName = "tasks")
data class Task(

    @PrimaryKey
    val id: String = "",

    val title: String,

    val description: String,

    val dueDate: LocalDate,

    val dueTime: LocalTime,
    val userId: Int
)