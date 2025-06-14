package com.example.remindernotes.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.asImageBitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.InputStream
import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.remindernotes.R
import com.example.remindernotes.ui.theme.ReminderNotesTheme
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import com.example.remindernotes.viewmodel.UserViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(userViewModel: UserViewModel, navController: NavController, isDarkTheme: MutableState<Boolean>) {
    val context = LocalContext.current
    val imageUri = remember { mutableStateOf<Uri?>(null) }

    val pickImageContract = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri: Uri? ->
        imageUri.value = uri
    }

    val image: Painter = imageUri.value?.let { uri ->
        val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
        val bitmap = BitmapFactory.decodeStream(inputStream)
        BitmapPainter(bitmap.asImageBitmap())
    } ?: painterResource(id = R.drawable.ic_launcher_background)
    val user by userViewModel.loggedInUser.collectAsState()

    ReminderNotesTheme(darkTheme = isDarkTheme.value) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Surface(
                modifier = Modifier.weight(1f),
                color = MaterialTheme.colorScheme.background
            ) {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { },
                            actions = {
                                IconButton(onClick = { isDarkTheme.value = !isDarkTheme.value }) {
                                    val icon = if (isDarkTheme.value) R.drawable.nightmode1 else R.drawable.nightmode
                                    Image(
                                        painter = painterResource(id = icon),
                                        contentDescription = if (isDarkTheme.value) "Switch to light mode" else "Switch to dark mode",
                                        modifier = Modifier.size(25.dp) // Adjust the size here
                                    )
                                }
                            }
                        )
                    },
                    content = {
                        if(!userViewModel.isLoggedIn()){
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(text = "You are not logged in", fontSize = 24.sp)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(text = "Login or create an account to view your profile", fontWeight = FontWeight.Light, textAlign = TextAlign.Center)
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
                        } else{
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp)
                                    .padding(top = 20.dp),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Image(
                                    painter = image,
                                    contentDescription = "Profile Picture",
                                    modifier = Modifier
                                        .size(200.dp)
                                        .clip(CircleShape)
                                        .background(Color.Gray)
                                        .clickable { pickImageContract.launch("image/*") }, // Launch the gallery when the image is clicked
                                    contentScale = ContentScale.Crop
                                )

                                Spacer(modifier = Modifier.height(25.dp))

                                Text(
                                    text = user?.name ?:"",
                                    fontSize = 30.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onBackground
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Spacer(modifier = Modifier.height(25.dp))

                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(MaterialTheme.colorScheme.surface)
                                        .padding(16.dp)
                                        .height(100.dp),
                                    elevation = CardDefaults.cardElevation(4.dp)
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxSize(),
                                        verticalArrangement = Arrangement.Center,
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        ContactInfoItem(icon = Icons.Filled.MailOutline, text = user?.email?:"No email")
                                    }
                                }

                                Spacer(modifier = Modifier.height(25.dp))

                                Button(
                                    onClick = { userViewModel.logout()},
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if(!isDarkTheme.value) MaterialTheme.colorScheme.primary else Color.Gray,
                                        contentColor = Color.White
                                    ),
                                    modifier = Modifier
                                        .width(300.dp)
                                        .height(60.dp)
                                        .clip(RoundedCornerShape(8.dp))

                                ) {
                                    Text("Log Out", fontSize = 21.sp)
                                }
                            }
                        }

                    }
                )
            }
            BottomNavigationBar(navController, isDarkTheme.value)
        }
    }
}

@Composable
fun ContactInfoItem(icon: ImageVector, text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        Spacer(modifier = Modifier.width(16.dp))
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = text,
            fontSize = 22.sp,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}