package com.yummybiteadmin.foodapp.screens.login

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.yummybiteadmin.foodapp.navigation.YummyBitesScreens

@Composable
fun LoginScreen(image1:Int, image2:Int,
          navController: NavController,viewModel: LoginScreenViewModel= hiltViewModel()){
    var email by remember{ mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwaordVisibility by remember{ mutableStateOf(false) }
    val keyboardController= LocalSoftwareKeyboardController.current
    val context= LocalContext.current
    Box(modifier = Modifier
        .fillMaxSize()
        .background(MaterialTheme.colors.primary)
    ,contentAlignment = Alignment.Center)
    {
        Image(painter = painterResource(image1), contentDescription = "Background Image",
         modifier =Modifier.fillMaxSize(), contentScale = ContentScale.Crop)

        Image(painter = painterResource(image2), contentDescription = "Logo Image",
          modifier = Modifier
              .size(155.dp)
              .align(Alignment.TopCenter)
              .padding(top = 6.dp))

        Column(modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()) {
            OutlinedTextField(value = email, onValueChange ={email=it}, label = { Text(text = "Email", color = Color.White)}
            , singleLine = true, textStyle = TextStyle(fontSize = 18.sp, color = Color.White), modifier = Modifier
                    .height(66.dp)
                    .fillMaxWidth()
                    .background(color = Color.Transparent), keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onNext = {keyboardController?.hide()}))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password", color = Color.White) },
                singleLine = true,
                textStyle = TextStyle(fontSize = 18.sp, color = Color.White),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(66.dp)
                    .background(color = Color.Transparent),
                visualTransformation = if (passwaordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        keyboardController?.hide()
                    }
                ),
                trailingIcon = {
                    IconButton(onClick = { passwaordVisibility = !passwaordVisibility }) {
                        Icon(
                            imageVector = if (passwaordVisibility) Icons.Default.Visibility else  Icons.Default.VisibilityOff,
                            contentDescription = "Password Visibility"
                        )


                    }
                }
            )
            Spacer(modifier = Modifier.height(16.dp))

            val isButtonEnabled =email.isNotBlank() && password.isNotBlank() && email.contains("@gmail.com") && password.length >=8
             RoundedButton(text = "Login", onClick = {
                 if(isButtonEnabled){ viewModel.signInUserWithEmailAndPassword(email = email,password=password) {
                 navController.navigate(YummyBitesScreens.BottomNavigationScreen.name)
             }

             }else {
                    if(email.isBlank()){
                     Toast.makeText(context,"Email field is empty",Toast.LENGTH_SHORT).show()
                      }
                     if(password.isBlank()){
                         Toast.makeText(context,"Password field is empty",Toast.LENGTH_SHORT).show()
                     }
                     if(!email.contains("@gmail.com")){
                         Toast.makeText(context,"Email is not correct",Toast.LENGTH_SHORT).show()

                     }
                     if(password.length < 8){
                         Toast.makeText(context,"Please enter the correct password",Toast.LENGTH_SHORT).show()

                     }
                     else{
                         Toast.makeText(context,"Please enter valid email id and password",Toast.LENGTH_SHORT).show()

                     }
                 }

                                                     },  )
            Spacer(modifier=Modifier.height(6.dp))

            Text(
                text = "Forgot Password",
                color = Color.Red,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                modifier = Modifier.clickable { navController.navigate(YummyBitesScreens.PasswordRecover.name) }
            )

            Spacer(modifier = Modifier.height(6.dp))
            Row(modifier = Modifier.align(Alignment.End)) {
                Text(
                    text = "New User? ",
                    color = Color.White,
                    fontSize = 14.sp
                )
                // Create Account Button
                Text(
                    text = " Create Account",
                    modifier = Modifier.clickable {
                        navController.navigate(YummyBitesScreens.SignUpScreen.name)
                    },
                    color = Color.Cyan,
                    fontSize = 14.sp
                )
            }
        }

        }
}

@Composable
fun RoundedButton(text: String, onClick: () -> Unit ) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(23.dp)
            .height(39.dp)
            .clip(RoundedCornerShape(12.dp))
        ,
        colors = ButtonDefaults.buttonColors(
            contentColor = Color.White, backgroundColor = MaterialTheme.colors.primary
        )
    ) {
        Text(
            text = text,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
