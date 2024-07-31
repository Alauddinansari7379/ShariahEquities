package com.example.tlismimoti.listing.model.cartModel

import com.sellacha.tlismiherbs.category.model.cartModel.Items

data class Data(
    val attributes: String,
    val count: Int,
    val items: Items,
    val options: String,
    val preview: String,
    val price: Int,
    val priceTotal: String,
    val qty: String,
    val subtotal: String,
    val tax: String,
    val term_id: Int,
    val term_title: String,
    val total: String,
    val user_id: String
)