package com.sellacha.tlismiherbs.cart.model

data class Data(
    val attributes: String,
    val count: Int,
    val items: ArrayList<Items>,
    val options: String,
    val preview: String,
    val price: Int,
    val priceTotal: String,
    val qty: String,
    val subtotal: String,
    val final_total: String,
    val tax: String,
    val term_id: Int,
    val term_title: String,
    val total: String,
    val user_id: Int
)