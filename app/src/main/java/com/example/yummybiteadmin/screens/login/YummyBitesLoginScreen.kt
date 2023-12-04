package com.example.yummybiteadmin.screens.login

import android.annotation.SuppressLint
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.yummybiteadmin.navigation.YummyBitesScreens


@Composable
fun YummyBitesLoginScreen(image1:Int,image2:Int,navController: NavHostController,viewModel: LoginScreenViewModel= androidx.lifecycle.viewmodel.compose.viewModel()) {
    val showLoginForm = rememberSaveable{
        mutableStateOf(false)
    }
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
                .height(359.dp)
                .padding(start = 18.dp, end = 18.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                if (showLoginForm.value) UserForm(
                    loading = false,
                    isCreateAccount = false
                ) { email, password ->
                    viewModel.signInUserWithEmailAndPassword(email = email, password = password) {
                        navController.navigate(YummyBitesScreens.BottomNavigationScreen.name)
                    }
                } else {
                    UserForm(loading = false, isCreateAccount = true) { email, password ->
                        viewModel.createUserWithEmailAndPassword(email, password) {
                            navController.navigate(YummyBitesScreens.BottomNavigationScreen.name)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(15.dp))
                if (showLoginForm.value) {
                    Text(
                        text = "Forgot Password",
                        modifier = Modifier
                            .padding(start = 230.dp)
                            .clickable { navController.navigate(YummyBitesScreens.PasswordRecover.name) },
                        color = Color.Red,
                        fontSize = 14.sp
                    )
                }
                Row(
                    modifier = Modifier.padding(15.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val text = if (showLoginForm.value) "Sign up" else "Login"
                    if(showLoginForm.value) {
                        Text(
                            text = "New User?",
                            color = Color.Gray,
                            fontSize = 15.sp,
                            modifier = Modifier.clickable {
                                showLoginForm.value = !showLoginForm.value
                            }
                        )
                    }
                    if(!showLoginForm.value){
                        Text(
                            text = "Old User?",
                            color = Color.Gray,
                            fontSize = 15.sp,
                            modifier = Modifier.clickable {
                                showLoginForm.value = !showLoginForm.value
                            }
                        )
                    }
                    Text(
                        text = text,
                        Modifier
                            .clickable { showLoginForm.value = !showLoginForm.value }
                            .padding(start = 5.dp),
                        fontWeight = FontWeight.Bold,
                        color = Color.Green,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }

}

fun forgotPassword(value: Boolean) {
    if(value){

    }
}

@SuppressLint("SuspiciousIndentation")
@OptIn(ExperimentalComposeUiApi::class)
@Preview
@Composable
fun UserForm(loading:Boolean=false,isCreateAccount:Boolean=false,
             onDone:(String,String) -> Unit={email,pwd->}) {
    val email = rememberSaveable { mutableStateOf("") }
    val password = rememberSaveable { mutableStateOf("") }
    val passwordVisibility = rememberSaveable { mutableStateOf(false) }
    val passwordFocusRequest = FocusRequester.Default
    val keyboardController = LocalSoftwareKeyboardController.current
    val valid = remember(email.value, password.value) {
        email.value.trim().isNotEmpty() && password.value.trim().isNotEmpty()
    }
    val modifier = Modifier
        .height(250.dp)
        .background(MaterialTheme.colors.background)
        .verticalScroll(rememberScrollState())
        .height(250.dp)
    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        val errorMessage =
            if (isCreateAccount) {
                buildAnnotatedString {
                    withStyle(style = SpanStyle(color = Color.Red, fontWeight = FontWeight.Thin, fontSize = 15.sp)) {
                        append("Please enter a valid email and password that is at least 6 characters")
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }
            } else {
                AnnotatedString("")
            }
        Text(
            text = errorMessage,
            modifier = Modifier.padding(3.dp),
            color = Color.Red,
            fontWeight = FontWeight.Bold
        )
        emailInput(emailState = email, enabled = true, onAction = KeyboardActions {
            passwordFocusRequest.requestFocus()
        })
        PasswordInput(modifier= Modifier.focusRequester(passwordFocusRequest),
            passwordState=password,
            labelId="Password",
            enabled=true,
            passwordVisibility=passwordVisibility,onAction= KeyboardActions{
                if(!valid) return@KeyboardActions
                onDone(email.value.trim(),password.value.trim())
            })
        Spacer(modifier = Modifier.width(12.dp))

        SubmitButton(textId = if(isCreateAccount)"Create Account" else "Login",loading=loading,
            validInputs=valid){
            onDone(email.value.trim(),password.value.trim())
            keyboardController?.hide()
        }
     }

}

@Composable
fun SubmitButton(textId: String, loading: Boolean, validInputs: Boolean,
                 onClick:()->Unit) {
    Button(onClick = onClick, modifier = Modifier
        .padding(2.dp)
        .width(290.dp)
         , enabled = !loading && validInputs, shape = RoundedCornerShape(5.dp)
    ) {
        if(loading) CircularProgressIndicator(modifier = Modifier.size(25.dp))
        else Text(text=textId, modifier = Modifier.padding(0.dp).align(Alignment.CenterVertically))

    }
}

@Composable
fun PasswordInput(modifier: Modifier, passwordState: MutableState<String>, labelId: String, enabled: Boolean, passwordVisibility: MutableState<Boolean>,
                  imeAction: ImeAction = ImeAction.Done,
                  onAction: KeyboardActions = KeyboardActions.Default) {
    val visualTransformation = if(passwordVisibility.value) VisualTransformation.None else
        PasswordVisualTransformation()
    OutlinedTextField(
        value = passwordState.value,
        onValueChange = { passwordState.value = it },
        modifier = Modifier
            .padding(bottom = 10.dp, start = 10.dp, end = 10.dp)
            .fillMaxWidth(),
        label = { Text(labelId) },
        singleLine = true,
        enabled = enabled,
        textStyle = TextStyle(fontSize = 18.sp, color = MaterialTheme.colors.onBackground),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = imeAction), visualTransformation =visualTransformation, trailingIcon ={PasswordVisibility(passwordVisibility=passwordVisibility)})
}

@Composable
fun PasswordVisibility(passwordVisibility: MutableState<Boolean>) {
    val visible = passwordVisibility.value
    IconButton(onClick = { passwordVisibility.value= !visible }) {
        Icons.Default.Close
    }
}


@Composable
fun emailInput(
    modifier: Modifier = Modifier,
    emailState: MutableState<String>,
    labelId: String = "Email",
    enabled: Boolean = true,
    imeAction: ImeAction = ImeAction.Next,
    onAction: KeyboardActions = KeyboardActions.Default
) {
    OutlinedTextField(
        value = emailState.value,
        onValueChange = { emailState.value = it },
        label = {
            Text(
                text = labelId,
                color = MaterialTheme.colors.onBackground
            )
        },
        singleLine = true,
        enabled = enabled,
        textStyle = TextStyle(fontSize = 18.sp, color = MaterialTheme.colors.onBackground),
        modifier = modifier
            .padding(bottom = 9.dp, start = 10.dp, end = 9.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp)),
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Email,
            imeAction = imeAction
        ),
        keyboardActions = onAction
    )
}


@Composable
fun InputField(
    modifier: Modifier = Modifier,
    valueState: MutableState<String>,
    labelId: String,
    enabled: Boolean,
    isSingleLine: Boolean = true,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Next,
    onAction: KeyboardActions = KeyboardActions.Default,
  //  trailingIcon:Int = R.drawable.baseline_email_24, // Add this parameter for the trailing icon
    onTrailingIconClick: () -> Unit = {}
) {
    OutlinedTextField(

        value = valueState.value,
        onValueChange = { valueState.value = it },
        label = { Text(labelId) },
        singleLine = isSingleLine,
        enabled = enabled,
        textStyle = TextStyle(fontSize = 18.sp, color = MaterialTheme.colors.onBackground),
        modifier = modifier
            .padding(bottom = 10.dp, start = 9.dp, end = 9.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(40.dp))
            .background(color = Color.White),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType, imeAction = imeAction),

        )
}

