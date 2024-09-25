package com.amtech.shariahEquities.profile.activity.model

data class Result(
    val amount: Int,
    val created_at: String,
    val email: String,
    val methodTransactionId: Any,
    val name: String,
    val paymentDate: String,
    val paymentId: Int,
    val paymentMethod: Any,
    val paymentStatus: String,
    val status: Int,
    val subscription_end_date: String,
    val subscription_start_date: String,
    val transaction_id: String,
    val updated_at: String,
    val user_id: Int
)