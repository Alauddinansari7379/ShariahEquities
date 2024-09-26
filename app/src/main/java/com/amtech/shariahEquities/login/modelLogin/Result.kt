package com.amtech.shariahEquities.login.modelLogin

data class Result(
    val Id: Int,
    val IsSubscribed: Int,
    val created_at: String,
    val email: String,
    val mobile_number: Long,
    val name: String,
    val subscription_start_date: String?,
    val subscription_end_date: String?,
    val otp: Any,
    val status: Int,
    val updated_at: String
)