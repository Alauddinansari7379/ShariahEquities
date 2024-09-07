package com.amtech.shariahEquities.modelCompany

data class Result(
    val complaint_type: Int,
    val id: Int,
    val name_of_company: String,
    val status: Int,
    val symbol: String
)