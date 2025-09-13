package com.yummybiteadmin.foodapp.screens.home

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.yummybiteadmin.foodapp.R
import com.yummybiteadmin.foodapp.model.Food
import com.yummybiteadmin.foodapp.navigation.YummyBitesScreens
import com.yummybiteadmin.foodapp.screens.adddishes.AddDishScreen
import com.yummybiteadmin.foodapp.screens.adddishes.AddDishViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.protobuf.DescriptorProtos.FieldDescriptorProto.Label
import kotlinx.coroutines.delay


@Composable
fun YummyBitesHomeScreen(navController: NavController,viewModel: AddDishViewModel= hiltViewModel()) {
    var selectedDish by remember { mutableStateOf<Food?>(null) }
     val context= LocalContext.current

        var showUpdateButton by remember{ mutableStateOf(false) }


    if (selectedDish != null) {
        val viewModel: AddDishViewModel = hiltViewModel()

        AddDishScreen(
            viewModel = viewModel,
            foodDetails = selectedDish,
            navCtlr=navController,
            showUpdateButton=showUpdateButton,
            onSave = { savedFood ->
                // Implement the logic you want to execute when a dish is saved

            },
            onCancel = {
                selectedDish = null
             },

            onDelete = {
                val foodToDelete = selectedDish
                if (foodToDelete != null) {
                    // Implement the logic to delete the item from the database
                    viewModel.deleteDishFromFirebase(foodToDelete)
                }

                // Update the UI or perform any other necessary actions
                selectedDish = null
                navController.navigate(YummyBitesScreens.HomeScreen.name)
             },


         )
    } else {
        HomeScreen(
            onDishClicked = { selectedFood ->
                selectedDish = selectedFood
                showUpdateButton=true
             },
            onDelete = {selectedFood->
                 viewModel.deleteDishFromFirebase(selectedFood)
            }
        )
    }
}


@Composable
fun HomeScreen(onDishClicked: (Food) -> Unit,onDelete: (Food) -> Unit) {
    val context = LocalContext.current
    var foodList by remember { mutableStateOf(emptyList<Food>()) }
    val user = Firebase.auth.currentUser
    val keyboardController = LocalSoftwareKeyboardController.current
    var searchText by remember {
        mutableStateOf("")
    }
    LaunchedEffect(user?.uid) {
        if (user != null) {
            // Fetch dishes from Firestore
            val db = FirebaseFirestore.getInstance()
            val dishesCollection = db.collection("admin").document(user.uid).collection("adminDishes")

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
            modifier = Modifier.padding(bottom = 6.dp)
        )
        Row(){
           OutlinedTextField(value = searchText, onValueChange = {searchText=it}, label = {Text(text = "Search Dish", fontStyle = FontStyle.Normal)},
               singleLine = true, modifier = Modifier
                   .height(64.dp)
                   .fillMaxWidth()
                 , keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text, imeAction = ImeAction.Search), keyboardActions = KeyboardActions(onNext ={keyboardController?.hide()}))
        }
        Spacer(modifier = Modifier.width(10.dp))
         LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            items(items = foodList.windowed(2, 2, partialWindows = true)) { rowItems ->
                val filteredRowItems = if (searchText.isNotBlank()) {
                    rowItems.filter { it.name.contains(searchText, ignoreCase = true) }
                } else {
                    rowItems
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    for (food in filteredRowItems) {
                        DishCard(food = food, onClick = { onDishClicked(food)
                           }, onDelte = {onDelete(food)})
                    }
                }
            }

        }
    }
}

@Composable
fun DishCard(
    food: Food,
    onClick: () -> Unit,
    onDelte:()->Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        backgroundColor = MaterialTheme.colors.background,
        modifier = Modifier
            .width(178.dp)
            .clickable(onClick = onClick), elevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize()
        ) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.TopEnd
            ) {
                IconButton(
                    onClick = onDelte,
                    modifier = Modifier.size(24.dp) // Adjust the size as needed
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = Color.Red
                    )
                }
            }
            Image(
                painter = painterResource(id = R.drawable.down),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .clip(CircleShape), // Circular shape
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row() {


                Column() {
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
