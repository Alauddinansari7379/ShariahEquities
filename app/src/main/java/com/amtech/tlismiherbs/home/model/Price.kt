package com.example.tlismimoti.home.model

data class Price(
    val ending_date: String,
    val id: Int,
    val price: Int,
    val price_type: Int,
    val regular_price: Int,
    val special_price: Int,
    val starting_date: String,
    val term_id: Int
)