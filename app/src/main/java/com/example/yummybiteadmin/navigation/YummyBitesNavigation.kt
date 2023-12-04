package com.example.yummybiteadmin.navigation

import YourMainScreen
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.yummybiteadmin.R
import com.example.yummybiteadmin.screens.YummyBitesSplashScreen
import com.example.yummybiteadmin.screens.login.YummyBitesLoginScreen
import com.example.yummybiteadmin.screens.password.PasswordCodeScreen
import com.example.yummybiteadmin.screens.password.RecoverPassword

@Composable
fun YummyBitesNavigation() {
    val navController= rememberNavController()
    val navHost= NavHost(navController = navController, startDestination = YummyBitesScreens.SplashScreen.name ){
       val image1= R.drawable.aa
        val image2= R.drawable.logo
        composable(YummyBitesScreens.SplashScreen.name){
            YummyBitesSplashScreen(navController=navController,image1,image2)
        }
        val imag1= R.drawable.aa
        val imag2= R.drawable.logo
        composable(YummyBitesScreens.LoginScreen.name){
            YummyBitesLoginScreen(navController=navController, image1 = imag1, image2 = imag2)
        }

        composable(YummyBitesScreens.PasswordRecover.name){
            RecoverPassword(navController=navController,imag1,imag2)
        }
        val imagee1=R.drawable.aa
            val imagee2=R.drawable.logo
        composable(YummyBitesScreens.PasswordCodeScreen.name){
            PasswordCodeScreen(navController=navController,imagee1,imagee2)
        }
        composable(YummyBitesScreens.BottomNavigationScreen.name) {
           // val yummybitesAction=YummyBitesActions(navController=navController)
            YourMainScreen()
        }
    }
}