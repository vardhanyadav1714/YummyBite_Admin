package com.example.yummybiteadmin.screens.password

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.firebase.auth.ActionCodeSettings
import com.google.firebase.auth.FirebaseAuth
import java.util.*

@Composable
fun RecoverPassword(navController: NavHostController,image1:Int,image2:Int) {
    var email= rememberSaveable { mutableStateOf("") }
    val auth=FirebaseAuth.getInstance()
    val code = generateRandomCode()

    val resetUrl = "https://example.com/reset-password?code=$code"

    val actionCodeSettings = ActionCodeSettings.newBuilder()
        .setUrl(resetUrl)
        .setHandleCodeInApp(true)
        .setAndroidPackageName(
            "com.example.yummybites",
          true,
            null
        )
        .build()
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        // Background Image (image1)
        Image(
            painter = painterResource(image1),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Logo Image (image2)
        Image(
            painter = painterResource(image2),
            contentDescription = null,
            modifier = Modifier
                .size(155.dp)
                .align(Alignment.TopCenter)
                .padding(top = 48.dp)
        )

        Card(
            shape = RoundedCornerShape(16.dp),
            elevation = 8.dp,
            backgroundColor = Color.White,
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp)
                .padding(start = 22.dp, end = 22.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {

                OutlinedTextField(
                    value = email.value,
                    onValueChange = { email.value = it },
                    label = { Text("Email") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                )


                Button(shape = RoundedCornerShape(5.dp),
                    onClick = {
                              if(email.value.isNotEmpty()){
                                  auth.sendPasswordResetEmail(email.value,actionCodeSettings)
                                      .addOnCompleteListener { task ->
                                          if (task.isSuccessful) {
                                              
                                              // Password reset email sent successfully
                                              // You can navigate the user to a screen to enter the reset code sent to their email
                                          } else {
                                              // Password reset email sending failed
                                              // Handle the error (e.g., display an error message to the user)
                                          }
                                      }
                              }

                    },
                    modifier = Modifier.fillMaxWidth().background(color = Color.LightGray)
                ) {
                    Text("Reset Password")
                }
            }
        }
    }}
fun generateRandomCode(): String {
    val random = Random()
    val code = (100000 + random.nextInt(900000)).toString() // Generates a 6-digit random number
    return code
}
