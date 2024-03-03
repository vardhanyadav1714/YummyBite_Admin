import android.annotation.SuppressLint
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.yummybiteadmin.navigation.YummyBitesScreens
import com.example.yummybiteadmin.screens.adddishes.AddDishScreen
import com.example.yummybiteadmin.screens.adddishes.AddDishViewModel
import com.example.yummybiteadmin.screens.cart.CartScreen
import com.example.yummybiteadmin.screens.home.YummyBitesHomeScreen
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState

import androidx.lifecycle.LiveData
import com.example.yummybiteadmin.model.Food
import com.example.yummybiteadmin.screens.orders.OrdersScreen

class YummyBitesActions(navController: NavController) {
    val openLogin: () -> Unit = {
        navController.navigate(YummyBitesScreens.LoginScreen.name)
    }
    val openHome: () -> Unit = {
        navController.navigate(YummyBitesScreens.HomeScreen.name)
    }
    val openProfile: () -> Unit = {
        navController.navigate(YummyBitesScreens.ProfileScreen.name)
    }
    val openOrders: () -> Unit = {
        navController.navigate(YummyBitesScreens.OrdersScreen.name)
    }
}

@SuppressLint("SuspiciousIndentation")
@Composable
fun YourMainScreen() {
    val navController = rememberNavController()

    val bottomNavigationScreens = listOf(
        YummyBitesScreens.HomeScreen.name,
        YummyBitesScreens.AddItemScreeen.name,
        YummyBitesScreens.OrdersScreen.name,
        YummyBitesScreens.ProfileScreen.name
    )
    val darkCyanColor = Color(0xFF008B8B)
    Scaffold(
        bottomBar = {
            BottomNavigation(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(2.dp))
                    .border(0.5.dp, color = Color.Cyan, shape = RoundedCornerShape(2.dp)),
                backgroundColor = Color.White
            ) {
                bottomNavigationScreens.forEach { screen ->
                    val isSelected = currentRoute(navController) == screen
                    BottomNavigationItem(
                        icon = {
                            Icon(
                                imageVector = provideIcon(screen),
                                contentDescription = null,
                                tint = if (isSelected) Color.Cyan else darkCyanColor
                            )
                        },
                        label = {
                            Text(
                                text = provideName(screen),
                                color = if (isSelected) Color.Cyan else darkCyanColor
                            )
                        },
                        selected = isSelected,
                        onClick = {
                            navController.navigate(screen) {
                                // Pop up to the start destination of the graph to
                                // avoid building up a large stack of destinations
                                // on the back stack as users select items
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                // Avoid multiple copies of the same destination when
                                // reselecting the same item
                                launchSingleTop = true
                                // Restore state when reselecting a previously selected item
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            // Content for the current screen
            NavHost(navController = navController, startDestination = YummyBitesScreens.HomeScreen.name) {
                composable(YummyBitesScreens.HomeScreen.name) {
                    YummyBitesHomeScreen(navController)
                }
                composable(YummyBitesScreens.AddItemScreeen.name) {
                    val viewmodel: AddDishViewModel = hiltViewModel()

                    val selectedFood: Food? by viewmodel.selectedFood.observeAsState()

                        AddDishScreen(
                            viewModel = viewmodel,
                            onSave = { savedFood ->
                                // saveDishToFirebase(savedFood)
                                // Implement the logic you want to execute when a dish is saved
                                // For example, you might want to update the UI or save the dish to Firebase
                                // saveDishToFirebase(savedFood)
                                // Update the UI or perform any other necessary actions
                            },
                            onCancel = {
                                navController.navigate(YummyBitesScreens.HomeScreen.name)
                                // Implement the logic when the user cancels adding a dish
                            },
                            onDelete = {
                                // Implement the logic to delete the item from the database
                                // You can use selectedFood to access the item to be deleted
                                val foodToDelete = selectedFood
                                if (foodToDelete != null) {
                                    viewmodel.deleteDishFromFirebase(foodToDelete)
                                }
                                // Update the UI or perform any other necessary actions
                            },
                            foodDetails = selectedFood,
                            showDeleteButton = true // Set this based on your condition
                        )

                }


                composable(YummyBitesScreens.OrdersScreen.name) {
                    OrdersScreen()
                }
                composable(YummyBitesScreens.ProfileScreen.name) {
                    ProfileScreen()
                }
            }
        }
    }
}

@Composable
fun currentRoute(navController: NavController): String? {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    return navBackStackEntry?.destination?.route
}


data class BottomNavigationItemInfo(
    val screen: YummyBitesScreens,
    val icon: ImageVector,
    val label: String
)


class BottomNavigationInfo(val screen: YummyBitesScreens,val icon:ImageVector,val name:String){

}

fun provideName(name: String): String {
    return when (name) {
        YummyBitesScreens.HomeScreen.name -> "Home"
        YummyBitesScreens.AddItemScreeen.name -> "Add Item"
        YummyBitesScreens.OrdersScreen.name -> "Orders"
        YummyBitesScreens.ProfileScreen.name -> "Profile"
        else -> "Home"
    }
}

// Define the provideIcon function
fun provideIcon(name: String): ImageVector {
    return when (name) {
        YummyBitesScreens.HomeScreen.name -> Icons.Default.Home
        YummyBitesScreens.AddItemScreeen.name -> Icons.Default.Add
        YummyBitesScreens.OrdersScreen.name -> Icons.Default.List
        YummyBitesScreens.ProfileScreen.name -> Icons.Default.Person
        else -> Icons.Default.Home
    }
}
