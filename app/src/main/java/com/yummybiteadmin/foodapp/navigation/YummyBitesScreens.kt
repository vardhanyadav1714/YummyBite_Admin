package com.yummybiteadmin.foodapp.navigation

enum class YummyBitesScreens {
    LoginScreen,
    HomeScreen,
    SplashScreen,
    DetailScreen,
    UpdateScreen,
    PasswordRecover,
    PasswordCodeScreen,
    BottomNavigationScreen,
    CartScreen,
    ProfileScreen,
    FavoriteScreen,
    SignUpScreen,
    AddItemScreeen,
    OrdersScreen,
    OrderUpdateScreen,
    SearchScreen;

    companion object{
        fun fromRoute(route:String):YummyBitesScreens=
            when(route.substringBefore("/")){
                LoginScreen.name->LoginScreen
                HomeScreen.name->HomeScreen
                SplashScreen.name->SplashScreen
                SearchScreen.name->SearchScreen
                DetailScreen.name->DetailScreen
                OrderUpdateScreen.name->OrderUpdateScreen
                UpdateScreen.name->UpdateScreen
                SignUpScreen.name->SignUpScreen
                PasswordRecover.name->PasswordRecover
                BottomNavigationScreen.name->BottomNavigationScreen
                PasswordCodeScreen.name->PasswordCodeScreen
                CartScreen.name->CartScreen
                ProfileScreen.name->ProfileScreen
                FavoriteScreen.name->FavoriteScreen
                OrdersScreen.name -> OrdersScreen
                AddItemScreeen.name->AddItemScreeen
                null->HomeScreen

                else -> throw IllegalArgumentException("route")
            }
    }
}