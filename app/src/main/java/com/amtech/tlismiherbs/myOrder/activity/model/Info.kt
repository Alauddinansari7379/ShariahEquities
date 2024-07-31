package com.example.tlismimoti.order.model

data class Info(
    val category_id: Int,
    val created_at: String,
    val title: String,
    val url: String,
    val amount: String,
      val customer: Customer,
    val customer_id: Int,
    val getway: Getway,
    val id: Int,
    val order_content: OrderContent,
    val order_item: List<OrderItem>,
    val order_no: String,
    val order_type: Int,
    val payment_status: Int,
    val s_date: Any,
    val shipping: Int,
    val shipping_info: Any,
    val status: String,
    val tax: Int,
    val total: Int,
    val transaction_id: Any,
    val updated_at: String,
    val user_id: Int
)