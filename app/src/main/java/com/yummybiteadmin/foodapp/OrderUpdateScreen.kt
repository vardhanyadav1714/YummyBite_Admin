package com.yummybiteadmin.foodapp

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import androidx.compose.ui.Modifier
import coil.compose.rememberImagePainter
import com.yummybiteadmin.foodapp.model.Food
import com.yummybiteadmin.foodapp.screens.adddishes.AddDishViewModel
import com.yummybiteadmin.foodapp.screens.adddishes.VendorSelectionDropdown
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


@Composable
fun OrderUpdateScreen(
    viewModel: AddDishViewModel,
    onUpdate: (Order) -> Unit,
    onCancel: () -> Unit,
    orderDetails: Order?,
) {
    // Extracting order details if not null, otherwise initializing with default value
    val orderId = remember { mutableStateOf(orderDetails?.orderId ?: "") }

    val userId = remember { mutableStateOf(orderDetails?.userId ?: "") }
    val userName = remember { mutableStateOf(orderDetails?.userName ?: "") }
    val vendorUserId = remember { mutableStateOf(orderDetails?.vendorUserId ?: "") }
    val vendorName = remember { mutableStateOf(orderDetails?.vendorName ?: "") }
    val totalAmount = remember { mutableStateOf(orderDetails?.totalAmount ?: 0.0) }
    val orderType = remember { mutableStateOf(orderDetails?.orderType ?: OrderTpe.PayAtOutlet) }
    val orderStatus = remember { mutableStateOf(orderDetails?.orderStatus ?: OrderStatus.Preparing) }
    val orderPayment = remember { mutableStateOf(orderDetails?.orderPayment ?: OrderPayment.Unpaid) }
    val foodList = remember { mutableStateOf(orderDetails?.foodList  ) }

    Column(
        modifier = Modifier

            .padding(16.dp)
    ) {
        Text(
            text = "Update Order",
            style = MaterialTheme.typography.h5,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colors.onSurface,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Non-editable fields
        Text(
            text = "User Name: ${userName.value}",
            style = MaterialTheme.typography.subtitle1,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colors.onSurface,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = "Vendor Name: ${vendorName.value}",
            style = MaterialTheme.typography.subtitle1,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colors.onSurface,
            modifier = Modifier.padding(bottom = 8.dp)
        )


        // Editable fields
        Text(
            text = "Total Amount: ${totalAmount.value}",
            style = MaterialTheme.typography.subtitle1,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colors.onSurface,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "OrderType : ${orderType.value.name}",
            style = MaterialTheme.typography.subtitle1,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colors.onSurface,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Spacer(modifier = Modifier.height(6.dp))

        EditableDropdown(
            value = orderStatus.value.name,
            onValueChange = { orderStatus.value = OrderStatus.valueOf(it) },
            label = "Order Status",
            options = OrderStatus.values().map { it.name },
         )
        Spacer(modifier = Modifier.height(6.dp))

        if(orderType.value.name==OrderTpe.PaidUsingRazorPay.name) {

            Text(
                text = "OrderPayment : ${orderType.value.name}",
                style = MaterialTheme.typography.subtitle1,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colors.onSurface,
                modifier = Modifier.padding(bottom = 8.dp)
            )

        }
        else {
            EditableDropdown(
                value = orderPayment.value.name,
                onValueChange = { orderPayment.value = OrderPayment.valueOf(it) },
                label = "Order Payment",
                options = OrderPayment.values().map { it.name },
            )
        }
        // Save and Cancel Buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = {
                    val updatedOrder = Order(
                        orderId=orderId.value,
                        userId = userId.value,
                        userName = userName.value,
                        vendorUserId = vendorUserId.value,
                        vendorName = vendorName.value,
                        foodList = emptyList(), // Empty list for now
                        totalAmount = totalAmount.value,
                        orderType = orderType.value,
                        orderStatus = orderStatus.value,
                        orderPayment = orderPayment.value
                    )
                    onUpdate(updatedOrder)
                },
                modifier = Modifier.width(120.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.Green) // Custom color
            ) {
                Text("Update", style = TextStyle(fontSize = 16.sp, color = Color.White)) // Custom color
            }

            Button(
                onClick = onCancel,
                modifier = Modifier.width(120.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red) // Custom color
            ) {
                Text("Cancel", style = TextStyle(fontSize = 16.sp, color = Color.White)) // Custom color
            }
        }
    }
}


@Composable
fun EditableDropdown(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    options: List<String>
) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        Text(
            text = label,
            style = TextStyle(
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colors.onSurface
            )
        )
        Box(
            modifier = Modifier
                .background(Color.LightGray)
                .padding(8.dp)
                .clickable { expanded = !expanded }
        ) {
            Text(
                text = value,
                style = TextStyle(
                    fontSize = 16.sp,
                    color = MaterialTheme.colors.onSurface
                )
            )
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        onClick = {
                            onValueChange(option)
                            expanded = false
                        }
                    ) {
                        Text(
                            text = option,
                            style = TextStyle(
                                fontSize = 16.sp,
                                color = MaterialTheme.colors.onSurface
                            )
                        )
                    }
                }
            }
        }
    }
}
