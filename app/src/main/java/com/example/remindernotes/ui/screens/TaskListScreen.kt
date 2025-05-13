package com.example.remindernotes.ui.screens


import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.remindernotes.data.Task
import com.example.remindernotes.ui.Screen
import com.example.remindernotes.utils.toCustomString
import com.example.remindernotes.viewmodel.TaskViewModel
import java.time.LocalDate
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import com.example.remindernotes.ui.theme.ReminderNotesTheme
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import com.example.remindernotes.viewmodel.UserViewModel
import java.time.YearMonth
import com.example.remindernotes.R

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")

@Composable
fun TaskListScreen(navController: NavController, taskViewModel: TaskViewModel, userViewModel: UserViewModel, isDarkTheme: MutableState<Boolean>) {
    var currentYearMonth by remember { mutableStateOf(YearMonth.now()) }

    val user by userViewModel.loggedInUser.collectAsState()
    var tasksForCurrentMonth by remember { mutableStateOf(emptyList<Task>()) }

    LaunchedEffect(user, currentYearMonth) {
        user?.let {
            tasksForCurrentMonth = taskViewModel.getTasksForUserByMonth(it.id, currentYearMonth)
            Log.d("TaskListScreen", "Tasks for current month: $tasksForCurrentMonth")
        }
    }




    ReminderNotesTheme(darkTheme = isDarkTheme.value){
        if(user!=null){
            Scaffold(
                topBar = {
                    Column {
                        TopAppBar(
                            modifier = Modifier.padding(top = 12.dp),
                            title = {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    val previousMonth = currentYearMonth.minusMonths(1)
                                    val nextMonth = currentYearMonth.plusMonths(1)

                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        IconButton(onClick = {
                                            currentYearMonth = previousMonth
                                        }) {
                                            Icon(
                                                imageVector = Icons.Default.ArrowBack,
                                                contentDescription = "Previous Month"
                                            )
                                        }
                                        Text(
                                            text = previousMonth.format(
                                                DateTimeFormatter.ofPattern(
                                                    "MMM"
                                                )
                                            ), style = MaterialTheme.typography.bodySmall
                                        )
                                    }

                                    Text(
                                        text = currentYearMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy")),
                                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                                    )

                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        IconButton(onClick = {
                                            currentYearMonth = nextMonth
                                        }) {
                                            Icon(
                                                imageVector = Icons.Default.ArrowForward,
                                                contentDescription = "Next Month"
                                            )
                                        }
                                        Text(
                                            text = nextMonth.format(DateTimeFormatter.ofPattern("MMM")),
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    }
                                }
                            }
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Divider(color = Color.Gray, thickness = 1.dp) // Adding Divider here
                    }
                },
                floatingActionButton = {
                    FloatingActionButton(onClick = {
                        navController.navigate("task_detail")
                    }) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Add Task")
                    }
                },
                bottomBar = { BottomNavigationBar(navController) }
            ) { innerPadding ->
                LazyColumn(
                    //columns = GridCells.Fixed(2),
                    modifier = Modifier.padding(innerPadding)
                ) {
                    items(tasksForCurrentMonth) { task ->
                        TaskItem(task = task, navController, taskViewModel)
                    }
                }
            }
        }else {
            Scaffold(
                content={
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "You are not logged in", fontSize = 24.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "Login or create an account to add tasks", fontWeight = FontWeight.Light)
                        Spacer(modifier = Modifier.height(24.dp))
                        Divider(
                            thickness = 2.dp,
                            modifier = Modifier.width(250.dp),
                            color = colorResource(id = R.color.defaultBlue))
                        Spacer(modifier = Modifier.height(24.dp))
                        Row(){
                            Button(onClick = { navController.navigate("register") },
                                colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.defaultBlue))) {
                                Text("Register")
                            }
                            Spacer(modifier = Modifier.width(24.dp))
                            Button(
                                onClick = { navController.navigate("login") },
                                colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.defaultBlue))) {
                                Text("Login")
                            }
                        }
                    }
                },
                bottomBar = { BottomNavigationBar(navController) }
            )
        }
    }
}

@Composable
fun SmallTaskItem(task: Task, navController: NavController, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .padding(vertical = 8.dp)
            .padding(horizontal = 8.dp)
            .fillMaxWidth()
            .clickable { navController.navigate("task_list") } // Add this line
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(imageVector = Icons.Default.Home, contentDescription = task.title)
                Spacer(modifier = Modifier.width(16.dp))
                Text(text = task.title, style = MaterialTheme.typography.headlineMedium)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Due Date: ${task.dueDate.toCustomString()}", style = MaterialTheme.typography.bodySmall)
            Text(text = "Due Time: ${task.dueTime.format(DateTimeFormatter.ofPattern("HH:mm"))}", style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
fun TaskItem(task: Task, navController: NavController, taskViewModel: TaskViewModel) {
    var showMenu by remember { mutableStateOf(false) }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .padding(horizontal = 8.dp)
            .clickable { showMenu = true }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically){
                Text(text = task.title, style = MaterialTheme.typography.headlineMedium)
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "",
                    modifier = Modifier.scale(1.2F))
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = task.description, style = MaterialTheme.typography.bodySmall)
            Spacer(modifier = Modifier.height(32.dp))
            Row (verticalAlignment = Alignment.CenterVertically) {
                Text(text = task.dueDate.toCustomString(), fontSize = 18.sp, fontWeight = FontWeight.Light)
                Spacer(modifier = Modifier.weight(1f))
                Text(text = task.dueTime.format(DateTimeFormatter.ofPattern("HH:mm")), fontSize = 18.sp)
            }
        }
        DropdownMenu(
            expanded = showMenu,
            onDismissRequest = { showMenu = false }
        ) {
            DropdownMenuItem(text = {
                Text("Edit")
            },onClick = {
                showMenu = false
                navController.navigate("task_edit/${task.id}")
            })
            DropdownMenuItem(text = {
                Text("Delete")
            },onClick = {
                showMenu = false
                taskViewModel.deleteTask(task)
            })
        }
    }
}

@Preview
@Composable
fun TaskPreview(){
    //TaskItem(task = Task(0, "Title1", "Description text", LocalDate.now(), LocalTime.now()))
}

@Composable
fun BottomNavigationBar(navController: NavController) {
    val items = listOf(
        BottomNavItem.Calendar,
        BottomNavItem.Home,
        BottomNavItem.Profile
    )

    BottomAppBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        items.forEachIndexed { index, item ->
            if (index == 1) {
                Spacer(Modifier.weight(1f, true))
            }
            IconButton(
                onClick = {
                    navController.navigate(item.route) {
                        navController.graph.startDestinationRoute?.let { route ->
                            popUpTo(route) {
                                saveState = true
                            }
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                modifier = Modifier.padding(horizontal = 35.dp) // Add padding here
            ) {
                Icon(imageVector = item.icon, contentDescription = item.title)
            }
            if (index == 1) {
                Spacer(Modifier.weight(1f, true))
            }
        }
    }
}

sealed class BottomNavItem(val title: String, val icon: ImageVector, val route: String) {
    object Home : BottomNavItem("Home", Icons.Filled.Home, Screen.Home.route)
    object Calendar : BottomNavItem("Calendar", Icons.Filled.DateRange, Screen.TaskList.route)
    object Profile : BottomNavItem("Profile", Icons.Filled.Person, Screen.Profile.route)
}