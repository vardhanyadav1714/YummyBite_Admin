package com.example.yummybiteadmin.screens.adddishes

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Image
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.KeyboardType.Companion.Email
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation.Companion.None
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberImagePainter
import com.example.yummybiteadmin.FirebaseManager
import com.example.yummybiteadmin.model.Food
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.*

@Composable
fun AddDishScreen(
    viewModel: AddDishViewModel,
    onSave: (Food) -> Unit,
    onCancel: () -> Unit,
    onDelete: () -> Unit,
    foodDetails: Food?,
    showDeleteButton: Boolean
) {
    var dishName by remember { mutableStateOf(TextFieldValue(text = foodDetails?.name ?: "")) }
    var priceText by remember { mutableStateOf(TextFieldValue(text = foodDetails?.price ?: "")) }
    var availability by remember { mutableStateOf(foodDetails?.availability ?: true) }
    val dishImageUri by remember { mutableStateOf<Uri?>(null) }
    var selectedVendor by remember { mutableStateOf("") }

    // Access the local context within the @Composable function
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Add Food Item Heading
        Text(
            text = "Add Food Item",
            style = TextStyle(
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier.padding(bottom = 16.dp)
        )
        // Dish Name TextField
        OutlinedTextField(
            value = dishName,
            onValueChange = { dishName = it },
            label = { Text("Dish Name") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )

        // Price TextField
        OutlinedTextField(
            value = priceText,
            onValueChange = { priceText = it },
            label = { Text("Price") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )

        // Availability Switch
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Availability")
            Switch(
                checked = availability,
                onCheckedChange = { availability = it }
            )
        }

        // Select Food Image Heading
//        Text(
//            text = "Select Food Image",
//            fontSize = 18.sp,
//            fontWeight = FontWeight.Bold,
//            modifier = Modifier
//                .padding(bottom = 16.dp)
//        )

        // Select Food Image Heading
        var imageUri by remember { mutableStateOf<Uri?>(null) }
        var placeholderImageUrl =
            "https://www.google.com/imgres?imgurl=https%3A%2F%2Fi0.wp.com%2Fpost.healthline.com%2Fwp-content%2Fuploads%2F2020%2F07%2F1296x728-header.jpg%3Fw%3D1155%26h%3D1528&tbnid=D2pYI6eClCZl7M&vet=12ahUKEwjl_OHSufSCAxXB9qACHdEDA5oQMygaegUIARCvAQ..i&imgrefurl=https%3A%2F%2Fwww.healthline.com%2Fnutrition%2Fis-thai-food-healthy&docid=kdRMbfm6anNPcM&w=1155&h=648&q=food%20images&ved=2ahUKEwjl_OHSufSCAxXB9qACHdEDA5oQMygaegUIARCvAQ"

//        Card(
//            modifier = Modifier
//                .height(50.dp)
//                .width(50.dp)
//                .padding(bottom = 16.dp)
//                .clickable {
//                    // Open the image picker dialog when clicked
//                    showImagePickerDialog(context) { uri ->
//                        imageUri = uri
//                    }
//                },
//            elevation = 4.dp,
//            shape = MaterialTheme.shapes.medium
//        ) {
//            Box(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .background(MaterialTheme.colors.background)
//            ) {
//                // Display Image or Placeholder
//                imageUri?.let {
//                    Image(
//                        painter = rememberImagePainter(data = it),
//                        contentDescription = null,
//                        modifier = Modifier
//                            .fillMaxSize()
//                            .clip(MaterialTheme.shapes.medium),
//                        contentScale = ContentScale.Crop
//                    )
//                } ?: run {
//                    // Placeholder or Icon
//                    Image(
//                        painter = rememberImagePainter(data = placeholderImageUrl),
//                        contentDescription = null,
//                        modifier = Modifier
//                            .fillMaxSize()
//                            .clip(MaterialTheme.shapes.medium),
//                        contentScale = ContentScale.Crop
//                    )
//                }
//                // Add an icon or text to indicate image selection
//                // For example, you can use an icon from Icons.Default.Add
//                Icon(
//                    imageVector = Icons.Default.Add,
//                    contentDescription = "Add Image",
//                    tint = MaterialTheme.colors.primary,
//                    modifier = Modifier
//                        .size(40.dp)
//                        .align(Alignment.Center)
//                )
//            }
//        }


        // Vendor Selection Dropdown
        VendorSelectionDropdown(
            vendors = listOf("Vendor 1", "Vendor 2", "Vendor 3", "Vendor 4"),
            onVendorSelected = { selectedVendor = it }
        )

        // Save and Cancel Buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(onClick = {
                val placeholderImageUrl="https://www.google.com/imgres?imgurl=https%3A%2F%2Fi0.wp.com%2Fpost.healthline.com%2Fwp-content%2Fuploads%2F2020%2F07%2F1296x728-header.jpg%3Fw%3D1155%26h%3D1528&tbnid=D2pYI6eClCZl7M&vet=12ahUKEwjl_OHSufSCAxXB9qACHdEDA5oQMygaegUIARCvAQ..i&imgrefurl=https%3A%2F%2Fwww.healthline.com%2Fnutrition%2Fis-thai-food-healthy&docid=kdRMbfm6anNPcM&w=1155&h=648&q=food%20images&ved=2ahUKEwjl_OHSufSCAxXB9qACHdEDA5oQMygaegUIARCvAQ"
                // Check if a dish with the same name already exists
                if (!viewModel.isDishNameExists(dishName.text)) {
                    val newDish = Food(
                        name = dishName.text,
                        price = priceText.text,
                        imageUrl = dishImageUri?.toString() ?: placeholderImageUrl,
                        availability = availability,
                        vendor = selectedVendor

                    )

                    viewModel.saveDishToFirebase(newDish)
                    onSave(newDish)

                    // Navigate back to HomeScreen
                    onCancel()
                } else {
                    // Display an error message or handle the case where the dish name already exists
                    // You can show a Snackbar or Toast message indicating that the dish already exists
                    // Modify this part based on your UI/UX requirements
                    // For example:
                    // Toast.makeText(context, "Dish with the same name already exists", Toast.LENGTH_SHORT).show()
                }
            }) {
                Text("Save")
            }

            // Display the Delete button only if showDeleteButton is true
//            if (showDeleteButton) {
//                Button(onClick = onDelete, colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red)) {
//                    Text("Delete")
//                }
//            }

            Button(onClick = onCancel) {
                Text("Cancel")
            }
        }
    }
}

@Composable
fun ImageSelection(
    onImageSelected: (Uri) -> Unit,
    modifier: Modifier = Modifier,
    activity: Activity
) {
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var placeholderImageUrl =
        "https://www.google.com/imgres?imgurl=https%3A%2F%2Fi0.wp.com%2Fpost.healthline.com%2Fwp-content%2Fuploads%2F2020%2F07%2F1296x728-header.jpg%3Fw%3D1155%26h%3D1528&tbnid=D2pYI6eClCZl7M&vet=12ahUKEwjl_OHSufSCAxXB9qACHdEDA5oQMygaegUIARCvAQ..i&imgrefurl=https%3A%2F%2Fwww.healthline.com%2Fnutrition%2Fis-thai-food-healthy&docid=kdRMbfm6anNPcM&w=1155&h=648&q=food%20images&ved=2ahUKEwjl_OHSufSCAxXB9qACHdEDA5oQMygaegUIARCvAQ"

    // Check if the activity is an instance of AppCompatActivity
    if (activity is AppCompatActivity) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            elevation = 4.dp,
            shape = MaterialTheme.shapes.medium
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable {
                        // Open the image picker dialog when clicked
                        showImagePickerDialog(activity, onImageSelected)
                    }
            ) {
                // Display Image or Placeholder
                imageUri?.let {
                    Image(
                        painter = rememberImagePainter(data = it),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(MaterialTheme.shapes.medium),
                        contentScale = ContentScale.Crop
                    )
                } ?: run {
                    // Placeholder or Icon
                    Image(
                        painter = rememberImagePainter(data = placeholderImageUrl),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(MaterialTheme.shapes.medium),
                        contentScale = ContentScale.Crop
                    )
                }
                // Add an icon or text to indicate image selection
                // For example, you can use an icon from Icons.Default.Add
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Image",
                    tint = MaterialTheme.colors.primary,
                    modifier = Modifier
                        .size(40.dp)
                        .align(Alignment.Center)
                )
            }
        }
    }
}


@Composable
fun VendorSelectionDropdown(
    vendors: List<String>,
    onVendorSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedVendor by remember { mutableStateOf("") }

    Column {
        Text("Vendor: $selectedVendor", modifier = Modifier.padding(8.dp))

        Box(
            modifier = Modifier
                .padding(8.dp)
                .background(Color.LightGray)
                .clickable { expanded = true }
        ) {
            Text(
                text = if (selectedVendor.isNotEmpty()) selectedVendor else "Select Vendor",
                modifier = Modifier.padding(8.dp)
            )
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                vendors.forEach { vendor ->
                    DropdownMenuItem(
                        onClick = {
                            selectedVendor = vendor
                            expanded = false
                            onVendorSelected(vendor)
                        }
                    ) {
                        Text(text = vendor)
                    }
                }
            }
        }
    }
}

fun showImagePickerDialog(context: Context, onImageSelected: (Uri) -> Unit) {
    val activity = context as? AppCompatActivity
    if (activity != null) {
        val getContent = activity.registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let { onImageSelected(it) }
        }

        // Open the image picker dialog
        getContent.launch("image/*")
    } else {
        // Handle the case where the context is not an AppCompatActivity
        // You might want to show an error message or take appropriate action
        Log.e("ShowImagePicker", "Context is not an AppCompatActivity")
    }
}

