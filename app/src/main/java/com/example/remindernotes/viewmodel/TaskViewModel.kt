package com.example.remindernotes.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.remindernotes.data.Task
import com.example.remindernotes.data.TaskDao
import com.example.remindernotes.data.UserDao
import com.example.remindernotes.repository.TaskRepository
import kotlinx.coroutines.launch
import java.time.YearMonth

class TaskViewModel(private val repository: TaskRepository): ViewModel(){
    val tasks = mutableStateListOf<Task>()

    init {
        viewModelScope.launch {
            tasks.addAll(repository.getAll())
        }
    }

    fun addTask(task: Task){

        viewModelScope.launch {
            repository.insertTask(task)
            tasks.add(task)
        }
    }

    suspend fun getTaskById(id: String): Task? {
        return repository.getTaskById(id)
    }

    fun editTask(task: Task) {
        viewModelScope.launch {
            repository.updateTask(task)
            val index = tasks.indexOfFirst { it.id == task.id }
            if (index != -1) {
                tasks[index] = task
            }
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            repository.deleteTask(task)
            tasks.remove(task)
        }
    }
    suspend fun getTasksForUser(userId: Int): List<Task> {
        return repository.getTasksForUser(userId)
    }
    suspend fun getTasksForUserByMonth(userId: Int, yearMonth: YearMonth): List<Task> {
        val tasksForUser = repository.getTasksForUser(userId)
        return tasksForUser.filter {
            it.dueDate.year == yearMonth.year && it.dueDate.monthValue == yearMonth.monthValue
        }
    }
}