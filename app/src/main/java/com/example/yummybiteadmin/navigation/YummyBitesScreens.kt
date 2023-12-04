package com.example.yummybiteadmin.navigation

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
    AddItemScreeen,
    OrdersScreen,
    SearchScreen;

    companion object{
        fun fromRoute(route:String):YummyBitesScreens=
            when(route.substringBefore("/")){
                LoginScreen.name->LoginScreen
                HomeScreen.name->HomeScreen
                SplashScreen.name->SplashScreen
                SearchScreen.name->SearchScreen
                DetailScreen.name->DetailScreen
                UpdateScreen.name->UpdateScreen
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