package com.example.remindernotes.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.remindernotes.ui.theme.ReminderNotesTheme
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.remindernotes.R
import com.example.remindernotes.data.Task
import java.time.LocalDateTime
import java.time.LocalDate
import com.example.remindernotes.viewmodel.TaskViewModel
import com.example.remindernotes.viewmodel.UserViewModel
import java.time.YearMonth
import java.time.format.DateTimeFormatter

fun LocalDate.toCustomString(): String {
    val dayFormat = when (this.dayOfMonth) {
        1, 21, 31 -> "st"
        2, 22 -> "nd"
        3, 23 -> "rd"
        else -> "th"
    }
    return "${this.month.name.lowercase().replaceFirstChar { it.uppercase() }} the ${this.dayOfMonth}$dayFormat ${this.year}"
}
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController, isDarkTheme: MutableState<Boolean>, taskViewModel: TaskViewModel, userViewModel: UserViewModel){
    val currentDateTime = remember { mutableStateOf(LocalDateTime.now()) }
    val user by userViewModel.loggedInUser.collectAsState()
    var tasksForNextWeek by remember { mutableStateOf(emptyList<Task>()) }
    var tasksForUser by remember { mutableStateOf(emptyList<Task>()) }

    LaunchedEffect(user) {
        user?.let {
            tasksForNextWeek = taskViewModel.getTasksForUserByMonth(it.id, YearMonth.now())
                .filter {
                    it.dueDate.isAfter(LocalDate.now()) && it.dueDate.isBefore(LocalDate.now().plusWeeks(1))
                }
            tasksForUser = taskViewModel.getTasksForUserByMonth(it.id, YearMonth.now())
        }
    }

    ReminderNotesTheme(darkTheme = isDarkTheme.value) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Surface(
                modifier = Modifier.weight(1f), // This will make the Surface take up all available space except for the BottomNavigationBar
                color = MaterialTheme.colorScheme.background
            ) {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = {
                                Text(
                                    text = "Home",
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        )
                    },
                    content = { // Move BottomNavigationBar inside the content lambda
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // First Card Section
                            Card(modifier = Modifier.padding(top = 70.dp, start = 16.dp, end = 16.dp).weight(1f).fillMaxWidth()) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(text = "Welcome back", style = MaterialTheme.typography.headlineMedium)
                                    Spacer(modifier = Modifier.height(16.dp)) // Add space here
                                    Text(text = "Today's Date: ${currentDateTime.value.toLocalDate().toCustomString()}", fontSize = 20.sp, fontWeight = FontWeight.Light)
                                    Spacer(modifier = Modifier.height(6.dp)) // Add space here
                                    Text(text = "Current Time: ${currentDateTime.value.toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm"))}", fontSize = 20.sp, fontWeight = FontWeight.Light)
                                }
                            }

                            // Second Card Section
                            Card(modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 48.dp).weight(1f).fillMaxWidth()) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(text = "Number of Tasks: ${tasksForUser.size}", style = MaterialTheme.typography.headlineMedium) // Adjust text size here
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text(text = "Number of Tasks this month: ${tasksForUser.count { it.dueDate.month == currentDateTime.value.month }}", fontSize = 20.sp, fontWeight = FontWeight.Light) // Adjust text size here
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(text = "Number of Tasks this week: ${tasksForUser.count { it.dueDate.isAfter(LocalDate.now()) && it.dueDate.isBefore(LocalDate.now().plusWeeks(1)) }}", fontSize = 20.sp, fontWeight = FontWeight.Light) // Adjust text size here
                                }
                            }

                            // Third Section (Not a Card)
                            Column(modifier = Modifier.weight(1f).fillMaxWidth()) {
                                Text(text = "UPCOMING TASKS", style = MaterialTheme.typography.headlineMedium, modifier = Modifier.align(Alignment.CenterHorizontally), fontWeight = FontWeight.Light)
                                Spacer(modifier = Modifier.height(16.dp))
                                LazyRow {
                                    items(tasksForNextWeek) { task ->
                                        SmallTaskItem(task = task,navController, Modifier.fillMaxWidth())
                                    }
                                }
                            }
                        }
                    }
                )
            }
            BottomNavigationBar(navController) // Place BottomNavigationBar outside the Surface
        }
    }
}