package com.amtech.shariahEquities.notification.modelwatchlist

data class Result(
    val company_id: Int,
    val complaint_type: Int,
    val created_at: String,
    val email: String,
    val id: Int,
    val name: String,
    val name_of_company: String,
    val status: Int,
    val symbol: String,
    val updated_at: String,
    val user_id: Int
)