package com.example.remindernotes.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.remindernotes.data.User
import com.example.remindernotes.viewmodel.UserViewModel
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewModelScope
import com.example.remindernotes.ui.theme.ReminderNotesTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun LoginScreen(
    viewModel: UserViewModel, navController: NavController, isDarkTheme: MutableState<Boolean>
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }
    var dialogMessage by remember { mutableStateOf("") }

    val loginStatusMessage by viewModel.loginStatusMessage.collectAsState()

    if (loginStatusMessage.isNotEmpty()) {
        dialogMessage = loginStatusMessage
        showDialog = true
    }

    ReminderNotesTheme(darkTheme = isDarkTheme.value){
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    }
                )
            },
            content = {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(text = "Login", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email") })
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") })
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = {
                        if (email.isBlank() || password.isBlank()) {
                            dialogMessage = "All fields must be filled"
                            showDialog = true
                        } else {
                            viewModel.viewModelScope.launch {
                                val user = viewModel.login(email, password)
                                if (user != null) {
                                    navController.navigate("home")
                                }
                            }
                        }
                    }) {
                        Text("Login")
                    }
                }
            })
    }


    if (showDialog) {
        AlertDialog(onDismissRequest = {
            viewModel.resetLoginStatusMessage()
            showDialog = false
        }, title = {
            Text(text = "Alert")
        }, text = {
            Text(dialogMessage)
        }, confirmButton = {
            Button(onClick = {
                viewModel.resetLoginStatusMessage()
                showDialog = false
            }) {
                Text("OK")
            }
        })
    }
}