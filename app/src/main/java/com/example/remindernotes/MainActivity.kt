package com.example.remindernotes

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.rememberNavController
import com.example.remindernotes.data.DatabaseProvider
import com.example.remindernotes.ui.NavGraph
import com.example.remindernotes.ui.theme.ReminderNotesTheme
import com.example.remindernotes.viewmodel.TaskViewModel
import com.example.remindernotes.viewmodel.TaskViewModelFactory
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.example.remindernotes.data.UserPreferences
import com.example.remindernotes.repository.TaskRepository
import com.example.remindernotes.viewmodel.UserViewModel
import com.example.remindernotes.viewmodel.UserViewModelFactory
import com.google.firebase.FirebaseApp

class MainActivity : ComponentActivity() {
    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FirebaseApp.initializeApp(this)
        val userDao = DatabaseProvider.getDatabase(this).userDao()

        val taskRepository = TaskRepository()
        val userPreferences = UserPreferences(this)
        val taskViewModel = ViewModelProvider(this, TaskViewModelFactory(taskRepository)).get(TaskViewModel::class.java)
        val userViewModel = ViewModelProvider(this, UserViewModelFactory(userPreferences)).get(UserViewModel::class.java)

        setContent {
            val isDarkTheme = remember { mutableStateOf(false) }
            MainContent(rememberNavController(), taskViewModel, userViewModel,isDarkTheme)
        }
    }

    @Composable
    fun MainContent(
        navController: NavHostController,
        taskViewModel: TaskViewModel,
        userViewModel: UserViewModel,
        isDarkTheme: MutableState<Boolean>) {
        ReminderNotesTheme(darkTheme = isDarkTheme.value) {
            Surface(color=MaterialTheme.colorScheme.background) {
                NavGraph(navController, taskViewModel, userViewModel, isDarkTheme)
            }
        }
    }
}


