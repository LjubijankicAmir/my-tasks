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
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.lifecycle.viewModelScope
import com.example.remindernotes.ui.theme.ReminderNotesTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun RegisterScreen(
    viewModel: UserViewModel, navController: NavController, isDarkTheme: MutableState<Boolean>
) {
    val coroutineScope = rememberCoroutineScope()
    var name by remember { mutableStateOf("") }
    var surname by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var password2 by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }
    var dialogMessage by remember { mutableStateOf("") }
    var successfullRegistration by remember { mutableStateOf(false) }

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
                    Text(text = "Register", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = name,
                        maxLines = 1,
                        onValueChange = { name = it },
                        label = { Text("Name") })
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = surname,
                        maxLines = 1,
                        onValueChange = { surname = it },
                        label = { Text("Surname") })
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = email,
                        maxLines = 1,
                        onValueChange = { email = it },
                        label = { Text("Email") })
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = password,
                        maxLines = 1,
                        visualTransformation = PasswordVisualTransformation(),
                        onValueChange = { password = it },
                        label = { Text("Password") })
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = password2,
                        maxLines = 1,
                        visualTransformation = PasswordVisualTransformation(),
                        onValueChange = { password2 = it },
                        label = { Text("Confirm password") })
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = {
                        if (name.isBlank() || surname.isBlank() || email.isBlank() || password.isBlank() || password2.isBlank()) {
                            dialogMessage = "All fields must be filled"
                            showDialog = true
                        } else if (password.length < 8) {
                            dialogMessage = "Password must be at least 8 characters long"
                            showDialog = true
                        } else if (password != password2) {
                            dialogMessage = "Passwords do not match"
                            showDialog = true
                        } else {

                            coroutineScope.launch {
                                try {
                                    viewModel.register(
                                        User(name = name, surname =  surname, email =  email, password = password)
                                    )
                                    dialogMessage = "Registration successful"
                                    successfullRegistration = true
                                } catch (e: Exception) {
                                    dialogMessage = "Registration failed: ${e.message}"
                                    successfullRegistration = false
                                } finally {
                                    showDialog = true
                                }
                            }
                        }
                    }, colors = ButtonDefaults.buttonColors(containerColor = if(!isDarkTheme.value) MaterialTheme.colorScheme.primary else Color.Gray)) {
                        Text("Register", color = Color.White)
                    }
                }
            })
    }

    if (showDialog) {
        AlertDialog(onDismissRequest = {
            showDialog = false
            if(successfullRegistration){
                navController.navigate("home")
            }
        }, title = {
            Text(text = "Alert")
        }, text = {
            Text(dialogMessage)
        }, confirmButton = {
            Button(onClick = {
                showDialog = false
                if(successfullRegistration){
                    navController.navigate("home")
                }
            }, colors = ButtonDefaults.buttonColors(containerColor = if(!isDarkTheme.value) MaterialTheme.colorScheme.primary else Color.Gray)) {
                Text("OK", color = Color.White)
            }
        })
    }
}