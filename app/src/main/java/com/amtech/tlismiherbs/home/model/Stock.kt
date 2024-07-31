package com.example.tlismimoti.home.model

data class Stock(
    val id: Int,
    val sku: String,
    val stock_manage: Int,
    val stock_qty: Int,
    val stock_status: Int,
    val term_id: Int
)