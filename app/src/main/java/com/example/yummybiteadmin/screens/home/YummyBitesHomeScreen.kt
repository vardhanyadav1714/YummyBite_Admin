package com.example.yummybiteadmin.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.yummybiteadmin.R
import com.example.yummybiteadmin.model.Food
import com.example.yummybiteadmin.screens.adddishes.AddDishScreen
import com.example.yummybiteadmin.screens.adddishes.AddDishViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

@Composable
fun YummyBitesHomeScreen(navController: NavController) {
    var selectedDish by remember { mutableStateOf<Food?>(null) }
    var showDeleteButton by remember { mutableStateOf(false) }

    if (selectedDish != null) {
        val viewModel: AddDishViewModel = hiltViewModel()

        AddDishScreen(
            viewModel = viewModel,
            foodDetails = selectedDish,
            onSave = { savedFood ->
                // Implement the logic you want to execute when a dish is saved
            },
            onCancel = {
                selectedDish = null
                showDeleteButton = false
            },
            onDelete = {
                val foodToDelete = selectedDish
                if (foodToDelete != null) {
                    // Implement the logic to delete the item from the database
                    viewModel.deleteDishFromFirebase(foodToDelete)
                }

                // Update the UI or perform any other necessary actions
                selectedDish = null
                showDeleteButton = false
            },


            showDeleteButton = showDeleteButton
        )
    } else {
        HomeScreen(
            onDishClicked = { selectedFood ->
                selectedDish = selectedFood
                showDeleteButton = true
            }
        )
    }
}

@Composable
fun HomeScreen(onDishClicked: (Food) -> Unit) {
    val context = LocalContext.current
    var foodList by remember { mutableStateOf(emptyList<Food>()) }
    val user = Firebase.auth.currentUser

    LaunchedEffect(user?.uid) {
        if (user != null) {
            // Fetch dishes from Firestore
            val db = FirebaseFirestore.getInstance()
            val dishesCollection = db.collection("dishes").document(user.uid).collection("user_dishes")

            dishesCollection.addSnapshotListener { value, error ->
                if (error != null) {
                    // Handle the error
                    return@addSnapshotListener
                }

                if (value != null) {
                    for (change in value.documentChanges) {
                        when (change.type) {
                            DocumentChange.Type.ADDED -> {
                                // Handle added document
                                val newFood = change.document.toObject(Food::class.java)
                                foodList = foodList + newFood
                            }
                            DocumentChange.Type.MODIFIED -> {
                                // Handle modified document
                                val modifiedFood = change.document.toObject(Food::class.java)
                                foodList = foodList.map { if (it.name == modifiedFood.name) modifiedFood else it }
                            }
                            DocumentChange.Type.REMOVED -> {
                                // Handle removed document
                                val removedFood = change.document.toObject(Food::class.java)
                                foodList = foodList.filter { it.name != removedFood.name }
                            }
                        }
                    }
                }
            }

        }
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Featured Dishes",
            style = TextStyle(
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            items(items = foodList.windowed(2, 2, partialWindows = true)) { rowItems ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    for (food in rowItems) {
                        DishCard(food = food, onClick = { onDishClicked(food) })
                    }
                }
            }
        }
    }
}

@Composable
fun DishCard(
    food: Food,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        backgroundColor = Color.White,
        modifier = Modifier
            .width(180.dp)
            .clickable(onClick = onClick)
            , elevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize()
        ) {
            Image(
                painter = painterResource(id = R.drawable.download),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .clip(CircleShape), // Circular shape
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = food.name,
                style = TextStyle(
                    color = Color.Gray,
                    fontSize = 16.sp
                )
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = food.price,
                style = TextStyle(
                    color = Color.Blue,
                    fontSize = 16.sp
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
            AvailabilityTextSurface(
                availability = food.availability,
                modifier = Modifier.align(Alignment.End)
            )
        }
    }
}

@Composable
fun AvailabilityTextSurface(
    availability: Boolean,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (availability) Color.Green else Color.Red
    val content = if (availability) "Available" else "Not Available"

    Surface(
        modifier = modifier.padding(4.dp),
        shape = RoundedCornerShape(12.dp),
        color = backgroundColor,
        contentColor = Color.White
    ) {
        Text(
            text = content,
            modifier = Modifier.padding(8.dp),
            fontSize = 12.sp
        )
    }
}
