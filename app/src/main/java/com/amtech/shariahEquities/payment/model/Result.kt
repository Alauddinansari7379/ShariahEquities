package com.amtech.shariahEquities.payment.model

data class Result(
    val amount: String,
    val created_at: String,
    val id: Int,
    val methodTransactionId: String,
    val paymentDate: String,
    val paymentMethod: String,
    val paymentStatus: String,
    val transaction_id: String,
    val updated_at: String,
    val user_id: String
)