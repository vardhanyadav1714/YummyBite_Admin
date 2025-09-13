package com.yummybiteadmin.foodapp

import com.yummybiteadmin.foodapp.model.Food

data class Order(
    val orderId:String="",
    val userId: String = "",
    val userName: String = "",
    val vendorUserId: String = "",
    val vendorName: String = "",
    val foodList: List<Food> = emptyList(),
    val totalAmount: Double = 0.0,
    val orderType: OrderTpe = OrderTpe.PayAtOutlet,
    val orderStatus: OrderStatus = OrderStatus.Preparing,
    val orderPayment: OrderPayment = OrderPayment.Unpaid
) {
    // No-argument constructor
    constructor() : this("","", "", "", "", emptyList(), 0.0)
}

enum class OrderStatus {
    Ready,
    Preparing,
    Delivered
}

enum class OrderPayment {
    Paid,
    Unpaid
}
enum class OrderTpe {
    PayAtOutlet,
    PaidUsingRazorPay
}