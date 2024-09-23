package com.amtech.shariahEquities.fragments.model.modelGetBasket

data class Result(
    val basketname: String,
    val companyid: String,
    val created_at: String,
    val description: String,
    val id: Int,
    val status: Int,
    val updated_at: String,
    val user_id: Int
)