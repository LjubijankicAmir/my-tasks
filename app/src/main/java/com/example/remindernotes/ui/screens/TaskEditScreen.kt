package com.example.remindernotes.ui.screens


import android.app.DatePickerDialog
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.remindernotes.data.Task
import com.example.remindernotes.viewmodel.TaskViewModel
import java.time.LocalDate
import java.util.Calendar
import android.app.TimePickerDialog
import android.text.format.DateFormat
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.filled.Create
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.LaunchedEffect
import com.example.remindernotes.ui.theme.ReminderNotesTheme
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.window.Dialog
import com.example.remindernotes.viewmodel.UserViewModel
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskEditScreen(
    navController: NavController,
    taskViewModel: TaskViewModel,
    isDarkTheme: MutableState<Boolean>,
    taskId: String
) {
    // 1) Hold the async-loaded Task
    var task by remember { mutableStateOf<Task?>(null) }

    // 2) Kick off the suspend fetch once
    LaunchedEffect(taskId) {
        task = taskViewModel.getTaskById(taskId)
    }

    // 3) If it’s still null, show a loading UI (or nothing)
    if (task == null) {
        // you can replace this with your own spinner / placeholder
        Box(
            Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    // 4) From here on `task!!` is non-null—seed your form state *once*
    val t = task!!
    var title by remember(t) { mutableStateOf(t.title) }
    var description by remember(t) { mutableStateOf(t.description) }
    var dueDate by remember(t) { mutableStateOf(t.dueDate) }
    var dueTime by remember(t) { mutableStateOf(t.dueTime) }
    val context = LocalContext.current

    fun showDatePicker() {
        val now = Calendar.getInstance()
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                dueDate = LocalDate.of(year, month + 1, dayOfMonth)
            },
            now.get(Calendar.YEAR),
            now.get(Calendar.MONTH),
            now.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    fun showTimePicker() {
        val now = LocalTime.now()
        val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hour, minute ->
            dueTime = LocalTime.of(hour, minute)
        }
        TimePickerDialog(
            context,
            timeSetListener,
            now.hour,
            now.minute,
            DateFormat.is24HourFormat(context)
        ).show()
    }

    ReminderNotesTheme(darkTheme = isDarkTheme.value){
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Edit Task") },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    }
                )
            },
            content = { paddingValues -> // Pass the padding values from Scaffold
                Column(
                    modifier = Modifier
                        .padding(paddingValues)
                        .padding(16.dp)
                ) { // Use both paddings
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Title") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Description") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = dueDate.toString(),
                        onValueChange = {/*Ignore*/ },
                        label = { Text("Due Date") },
                        readOnly = true,
                        trailingIcon = {
                            IconButton(onClick = { showDatePicker() }) {
                                Icon(imageVector = Icons.Default.DateRange, contentDescription = "")
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = dueTime.format(DateTimeFormatter.ofPattern("HH:mm")),
                        onValueChange = {/*Ignore*/ },
                        label = { Text("Time") },
                        readOnly = true,
                        trailingIcon = {
                            IconButton(onClick = { showTimePicker() }) {
                                Icon(imageVector = Icons.Default.Create, contentDescription = "")
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            task?.let {
                                val updatedTask = Task(
                                    id = it.id,
                                    title = title,
                                    description = description,
                                    dueDate = dueDate,
                                    dueTime = dueTime,
                                    userId = it.userId
                                )
                                taskViewModel.editTask(updatedTask)
                            }
                            navController.popBackStack()
                        },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("Edit task")
                    }
                }
            }
        )
    }
}
