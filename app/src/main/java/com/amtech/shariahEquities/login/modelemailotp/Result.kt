package com.amtech.shariahEquities.login.modelemailotp

data class Result(
    val email: String,
    val is_available: Int,
    val otp: Int
)