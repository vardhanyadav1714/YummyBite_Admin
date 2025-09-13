package com.yummybiteadmin.foodapp.screens.orders

import android.annotation.SuppressLint
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.yummybiteadmin.foodapp.Order
import com.yummybiteadmin.foodapp.OrderPayment
import com.yummybiteadmin.foodapp.OrderTpe
import com.yummybiteadmin.foodapp.OrderUpdateScreen
import com.yummybiteadmin.foodapp.navigation.YummyBitesScreens
import com.yummybiteadmin.foodapp.screens.adddishes.AddDishViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun OrderScreen(viewModel: AddDishViewModel = hiltViewModel(),navController: NavController) {
    val selectedOrder = remember { mutableStateOf<Order?>(null) }

    LaunchedEffect(key1 = true) {
        viewModel.fetchAllOrders()
    }

    val userOrders by viewModel.allorders.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Scaffold(
        backgroundColor = MaterialTheme.colors.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            } else {
                if (userOrders.isNotEmpty()) {
                    Text(
                        text = "Your Orders",
                        style = MaterialTheme.typography.h6,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colors.primary,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    LazyColumn {
                        items(userOrders) { order ->
                            OrderItem(order = order, isSelected = order == selectedOrder.value) {
                                selectedOrder.value = order
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                } else {
                    Text(
                        text = "No orders found",
                        style = MaterialTheme.typography.body1,
                        color = MaterialTheme.colors.onSurface
                    )
                }
            }
        }
    }

    selectedOrder.value?.let { order ->
        Dialog(
            onDismissRequest = { selectedOrder.value = null },
            content = {
                Column(
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .background(MaterialTheme.colors.surface, RoundedCornerShape(16.dp))
                ) {
                    Spacer(modifier = Modifier.height(16.dp))
                    OrderUpdateScreen(
                        viewModel = viewModel,
                        onUpdate = {

                            viewModel.updateOrder(it)
                            CoroutineScope(Dispatchers.Default).launch {
                                delay(3000)

                            }
                             navController.popBackStack()
                        },
                        onCancel = { selectedOrder.value = null },
                        orderDetails = order
                    )
                }
            }
        )
    }

}

@Composable
fun OrderItem(order: Order, isSelected: Boolean, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }, // Invoke the onClick lambda when clicked
        elevation = 8.dp,
        backgroundColor = MaterialTheme.colors.background,
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
             val myorder=Order(
                 orderId=order.orderId,
                userId = order.userId,
                userName =order.userName,
                vendorUserId = order.vendorUserId ?: "",
                vendorName = order.vendorName ?: "",
                foodList = order.foodList, // Use cart items as food list
                totalAmount = order.totalAmount,
                orderType = order.orderType, // Payment type determines order type
                orderPayment = if (order.orderType == OrderTpe.PaidUsingRazorPay) OrderPayment.Paid else OrderPayment.Unpaid,
                orderStatus = order.orderStatus
            )
            order.foodList.forEach { food ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()

                        .padding(vertical = 8.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = "Order From :${order.userName}",
                        style = MaterialTheme.typography.subtitle2,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colors.primary,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${food.name} x ${food.quantity}",
                            style = MaterialTheme.typography.subtitle1,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colors.onSurface
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Text(
                            text = "₹${"%.2f".format(food.totalPrice)}",
                            style = MaterialTheme.typography.body2,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colors.secondary
                        )
                    }

                }
            }

            Spacer(modifier = Modifier.height(8.dp))


            Text(
                text = "Order Type: ${order.orderType.name}",
                style = MaterialTheme.typography.body2,
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                color = MaterialTheme.colors.onSurface
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Display order payment status
            val paymentColor = if (order.orderPayment == OrderPayment.Paid) Color.Green else Color.Red
            Text(
                text = "Payment Status: ${order.orderPayment.name}",
                style = MaterialTheme.typography.body2,
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                color = paymentColor
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Display order status
            Text(
                text = "Order Status: ${order.orderStatus.name}",
                style = MaterialTheme.typography.body2,
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                color = MaterialTheme.colors.onSurface
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Display total amount
            Text(
                text = "Total Amount: ₹${"%.2f".format(order.totalAmount)}",
                style = MaterialTheme.typography.h6,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colors.secondary
            )
        }
    }
}
