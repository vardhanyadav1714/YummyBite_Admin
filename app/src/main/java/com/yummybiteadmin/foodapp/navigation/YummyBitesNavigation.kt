package com.yummybiteadmin.foodapp.navigation

import YourMainScreen
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.yummybiteadmin.foodapp.R
import com.yummybiteadmin.foodapp.screens.YummyBitesSplashScreen
import com.yummybiteadmin.foodapp.screens.login.LoginScreen

import com.yummybiteadmin.foodapp.screens.password.PasswordCodeScreen
import com.yummybiteadmin.foodapp.screens.password.RecoverPassword
import com.yummybiteadmin.foodapp.screens.signUpScreen

@Composable
fun YummyBitesNavigation() {
    val navController= rememberNavController()
    val navHost= NavHost(navController = navController, startDestination = YummyBitesScreens.SplashScreen.name ){
       val image1= R.drawable.aa
        val image2= R.drawable.logo
        composable(YummyBitesScreens.SplashScreen.name){
            YummyBitesSplashScreen(navController=navController,image1,image2)
        }


//        composable(YummyBitesScreens.PasswordRecover.name){
//            RecoverPassword(navController=navController,imag1,imag2)
//        }
        val imagee1=R.drawable.aa
            val imagee2=R.drawable.logo
        composable(YummyBitesScreens.PasswordCodeScreen.name){
            PasswordCodeScreen(navController=navController,imagee1,imagee2)
        }
        composable(YummyBitesScreens.BottomNavigationScreen.name) {
           // val yummybitesAction=YummyBitesActions(navController=navController)
            YourMainScreen(navctlr=navController)
        }
        composable(YummyBitesScreens.SignUpScreen.name) {
            val ima1= R.drawable.aa
            val ima2= R.drawable.logo
            signUpScreen(image1 = ima1, image2 = ima2, navController = navController)
        }
        composable(YummyBitesScreens.LoginScreen.name) {
            val ima1= R.drawable.aa
            val ima2= R.drawable.logo
            LoginScreen(image1 = ima1, image2 = ima2, navController = navController)
        }
        composable(YummyBitesScreens.PasswordRecover.name){
            val ima1= R.drawable.aa
            val ima2= R.drawable.logo
            RecoverPassword(navController = navController, image1 = ima1, image2 =ima2 )
        }
    }
}