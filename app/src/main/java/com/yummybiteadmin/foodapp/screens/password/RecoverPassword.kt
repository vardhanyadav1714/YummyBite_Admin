package com.yummybiteadmin.foodapp.screens.password
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.yummybiteadmin.foodapp.navigation.YummyBitesScreens
 import com.google.firebase.auth.ActionCodeSettings
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay
import java.util.*

@Composable
fun RecoverPassword(navController: NavHostController, image1: Int, image2: Int) {
    var email = rememberSaveable { mutableStateOf("") }
    val auth = FirebaseAuth.getInstance()
    val context = LocalContext.current
    Box(
        modifier = Modifier.fillMaxSize().background(Color.Transparent),
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


        // Inside your RecoverPassword composable
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
            modifier = Modifier.padding(vertical = 16.dp)
        ) {
            // Title Text

            // Email Field
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
                    .padding(16.dp)
            )

            // Styled Button
            RoundedButton(text = "Reset Password") { // Using RoundedButton composable
                if (email.value.isNotEmpty()) {
                    auth.sendPasswordResetEmail(email.value.toString())
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(
                                    context,
                                    "Password Reset Email Sent Successfully. Check your Email.",
                                    Toast.LENGTH_SHORT
                                ).show()
                                navController.navigate(YummyBitesScreens.LoginScreen.name)
                            } else {
                                val errorMessage =
                                    task.exception?.message ?: "Something went wrong."
                                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }
                } else {
                    Toast.makeText(
                        context,
                        "Please enter your email address.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

    }
}

@Composable
fun RoundedButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(23.dp)
            .height(39.dp)
            .clip(RoundedCornerShape(12.dp))
        ,
        colors = ButtonDefaults.buttonColors(
            contentColor = Color.White
        )
    ) {
        Text(
            text = text,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
