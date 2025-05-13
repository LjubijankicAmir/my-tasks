package com.example.remindernotes.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.remindernotes.ui.screens.TaskDetailScreen
import com.example.remindernotes.ui.screens.TaskListScreen
import com.example.remindernotes.ui.screens.ProfileScreen
import com.example.remindernotes.viewmodel.TaskViewModel
import androidx.compose.runtime.MutableState
import com.example.remindernotes.ui.screens.HomeScreen
import com.example.remindernotes.ui.screens.LoginScreen
import com.example.remindernotes.ui.screens.RegisterScreen
import com.example.remindernotes.ui.screens.TaskEditScreen
import com.example.remindernotes.viewmodel.UserViewModel

sealed class Screen(val route: String) {

    object Home : Screen("home")
    object TaskList : Screen("task_list")
    object TaskDetail : Screen("task_detail")
    object Calendar : Screen("calendar")
    object Profile : Screen("profile")
    object TaskEdit : Screen("task_edit")
    object Register : Screen("register")
    object Login : Screen("login")
}

@Composable
fun NavGraph(navController: NavHostController, taskViewModel: TaskViewModel, userViewModel: UserViewModel, isDarkTheme: MutableState<Boolean>) {
    NavHost(navController = navController, startDestination = Screen.Home.route) {
        composable(Screen.TaskList.route) {
            TaskListScreen(navController, taskViewModel, userViewModel,isDarkTheme)
        }
        composable(Screen.TaskDetail.route) {
            TaskDetailScreen(navController, taskViewModel, userViewModel, isDarkTheme)
        }
        composable(Screen.Profile.route) {
            ProfileScreen(userViewModel, navController, isDarkTheme)
        }
        composable(Screen.Home.route) {
            HomeScreen(navController, isDarkTheme, taskViewModel, userViewModel)
        }
        composable(Screen.Register.route) {
            RegisterScreen(userViewModel, navController, isDarkTheme)
        }
        composable("login") {
            LoginScreen(userViewModel, navController, isDarkTheme)
        }
        composable("task_edit/{taskId}") { backStackEntry ->
            val taskId = backStackEntry.arguments?.getString("taskId")
            TaskEditScreen(navController, taskViewModel, isDarkTheme, taskId!!)
        }
    }
}