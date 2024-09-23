package com.amtech.shariahEquities.notification.modelwatchlist

data class Result(
    val complaint_type: Int,
    val id: Int,
    val name_of_company: String,
    val status: Int,
    val symbol: String,
    val industry_group: String,
    val main_product_service_group: String,
    val nse_symbol_bse_script_id: String,
    val exchange: String,
    val final: String,
    val debts_market_cap: String,
    val compliant_debts_market_cap_status: String,
    val interest_bearing_securities_market_cap: String,
    val compliant_status_interest_bearing_securities_market_cap: String,
    val interest_income: String,
    val compliant_status_interest_income: String,
    val financial_screening: String,
    val created_at: String,
)