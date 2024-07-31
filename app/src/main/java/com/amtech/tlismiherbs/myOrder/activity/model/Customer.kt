package com.example.tlismimoti.order.model

data class Customer(
    val created_at: String,
    val created_by: Int,
    val deactivate: Int,
    val domain_id: Int,
    val email: String,
    val id: Int,
    val mobile: String,
    val name: String,
    val updated_at: String
)