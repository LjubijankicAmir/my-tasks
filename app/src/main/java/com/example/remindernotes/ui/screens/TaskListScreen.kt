package com.example.remindernotes.ui.screens


import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.res.fontResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow
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
                                                contentDescription = "Previous Month",
                                                tint = if(!isDarkTheme.value) MaterialTheme.colorScheme.primary else Color.White
                                            )
                                        }
                                        Text(
                                            text = previousMonth.format(
                                                DateTimeFormatter.ofPattern(
                                                    "MMM"
                                                )
                                            ), style = MaterialTheme.typography.bodySmall, color = if(!isDarkTheme.value) MaterialTheme.colorScheme.primary else Color.White
                                        )
                                    }

                                    Text(
                                        text = currentYearMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy")),
                                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = if(!isDarkTheme.value) MaterialTheme.colorScheme.primary else Color.White),
                                    )

                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        IconButton(onClick = {
                                            currentYearMonth = nextMonth
                                        }) {
                                            Icon(
                                                imageVector = Icons.Default.ArrowForward,
                                                contentDescription = "Next Month",
                                                tint = if(!isDarkTheme.value) MaterialTheme.colorScheme.primary else Color.White
                                            )
                                        }
                                        Text(
                                            text = nextMonth.format(DateTimeFormatter.ofPattern("MMM")),
                                            style = MaterialTheme.typography.bodySmall,
                                            color = if(!isDarkTheme.value) MaterialTheme.colorScheme.primary else Color.White
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
                    }, containerColor = if(!isDarkTheme.value) MaterialTheme.colorScheme.primary else Color.Gray ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Add Task")
                    }
                },
                bottomBar = { BottomNavigationBar(navController, isDarkTheme.value) }
            ) { innerPadding ->
                LazyColumn(
                    //columns = GridCells.Fixed(2),
                    modifier = Modifier.padding(innerPadding)
                ) {
                    items(tasksForCurrentMonth) { task ->
                        TaskItem(task = task, navController, taskViewModel, isDarkTheme.value)
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
                        HorizontalDivider(
                            modifier = Modifier.width(250.dp),
                            thickness = 2.dp,
                            color = if(!isDarkTheme.value) MaterialTheme.colorScheme.primary else Color.Gray
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Row(){
                            Button(onClick = { navController.navigate("register") },
                                colors = ButtonDefaults.buttonColors(containerColor = if(!isDarkTheme.value) MaterialTheme.colorScheme.primary else Color.Gray)) {
                                Text("Register", color = Color.White)
                            }
                            Spacer(modifier = Modifier.width(24.dp))
                            Button(
                                onClick = { navController.navigate("login") },
                                colors = ButtonDefaults.buttonColors(containerColor = if(!isDarkTheme.value) MaterialTheme.colorScheme.primary else Color.Gray)) {
                                Text("Login", color = Color.White)
                            }
                        }
                    }
                },
                bottomBar = { BottomNavigationBar(navController, isDarkTheme.value ) }
            )
        }
    }
}

@Composable
fun SmallTaskItem(task: Task, navController: NavController, modifier: Modifier = Modifier, isDarkTheme: Boolean) {
    Card(
        modifier = modifier
            .padding(vertical = 8.dp)
            .padding(horizontal = 8.dp)
            .fillMaxWidth()
            .clickable { navController.navigate("task_list") },
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDarkTheme) Color(0xFF1E1E1E) else Color.White,
        )
    // Add this line
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier
                    .size(36.dp)
                    .background(
                        color = if (isDarkTheme) Color(0xFF2E2E2E) else MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(8.dp)
                    ),
                    contentAlignment = Alignment.Center){
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = "Task Icon",
                        tint = if (isDarkTheme) Color.White else MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = task.title, style = MaterialTheme.typography.headlineMedium, color = if (isDarkTheme) Color.White else MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Due Date: ${task.dueDate.toCustomString()}", style = MaterialTheme.typography.bodySmall)
            Text(text = "Due Time: ${task.dueTime.format(DateTimeFormatter.ofPattern("HH:mm"))}", style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
fun TaskItem(task: Task, navController: NavController, taskViewModel: TaskViewModel, isDarkTheme: Boolean) {
    var showMenu by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .clickable { showMenu = true },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDarkTheme) Color(0xFF1E1E1E) else Color.White,
        )
    ) {
        Row (
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Spacer(modifier = Modifier.width(12.dp))
            Box(modifier = Modifier
                .size(48.dp)
                .background(
                    color = if (isDarkTheme) Color(0xFF2E2E2E) else MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp)
                ),
                contentAlignment = Alignment.Center){
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = "Task Icon",
                    tint = if (isDarkTheme) Color.White else MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(36.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column (
                modifier = Modifier.weight(1f),
            ) {
                Text(
                    text = task.title,
                    fontFamily = FontFamily(Font(R.font.poppins)),
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = if (isDarkTheme) Color.White else MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(vertical = 2.dp)
                )
                Text(
                    text = task.description,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    color = if (isDarkTheme) Color.LightGray else Color.Gray,
                    style = MaterialTheme.typography.bodySmall,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row {
                    Text(
                        text = task.dueDate.toCustomString(),
                        fontWeight = FontWeight.Bold,
                        color = if (isDarkTheme) Color.LightGray else MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = " - ${task.dueTime.format(DateTimeFormatter.ofPattern("HH:mm"))}",
                        fontWeight = FontWeight.Bold,
                        color = if (isDarkTheme) Color.LightGray else MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            IconButton(
                onClick = { showMenu = true },
                modifier = Modifier.scale(0.8f) // Scale down the icon
            ) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "More options",
                    tint = if (isDarkTheme) Color.White else MaterialTheme.colorScheme.primary
                )
            }
        }

        // Dropdown menu (edit/delete)
        DropdownMenu(
            expanded = showMenu,
            onDismissRequest = { showMenu = false }
        ) {
            DropdownMenuItem(
                text = { Text("Edit") },
                onClick = {
                    showMenu = false
                    navController.navigate("task_edit/${task.id}")
                }
            )
            DropdownMenuItem(
                text = { Text("Delete") },
                onClick = {
                    showMenu = false
                    taskViewModel.deleteTask(task)
                }
            )
        }
    }
}

@Preview
@Composable
fun TaskPreview(){
    //TaskItem(task = Task(0, "Title1", "Description text", LocalDate.now(), LocalTime.now()))
}

@Composable
fun BottomNavigationBar(
    navController: NavController,
    isDarkTheme: Boolean
) {
    val items = listOf(
        BottomNavItem.Calendar,
        BottomNavItem.Home,
        BottomNavItem.Profile
    )
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val backgroundColor = if (isDarkTheme) Color(0xFF121212) else MaterialTheme.colorScheme.primary
    val contentColor = Color.White

    BottomAppBar(
        containerColor = backgroundColor,
        contentColor = contentColor,
        tonalElevation = 8.dp
    ) {
        items.forEach { item ->
            val selected = currentRoute == item.route
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clickable {
                        navController.navigate(item.route) {
                            navController.graph.startDestinationRoute?.let { route ->
                                popUpTo(route) { saveState = true }
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = item.title,
                    tint = contentColor
                )
                Text(
                    text = item.title,
                    fontSize = 12.sp,
                    color = contentColor,
                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                )
            }
        }
    }
}


sealed class BottomNavItem(val title: String, val icon: ImageVector, val route: String) {
    object Home : BottomNavItem("Home", Icons.Filled.Home, Screen.Home.route)
    object Calendar : BottomNavItem("Tasks", Icons.Filled.DateRange, Screen.TaskList.route)
    object Profile : BottomNavItem("Profile", Icons.Filled.Person, Screen.Profile.route)
}