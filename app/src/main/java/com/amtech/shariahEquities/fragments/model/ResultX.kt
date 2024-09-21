package com.amtech.shariahEquities.fragments.model

data class ResultX(
    val compliant_debts_market_cap_status: String,
    val compliant_status_interest_bearing_securities_market_cap: String,
    val compliant_status_interest_income: String,
    val created_at: String,
    val debts_market_cap: String,
    val exchange: String,
    val `final`: String,
    val financial_screening: String,
    val id: Int,
    val industry_group: String,
    val interest_bearing_securities_market_cap: String,
    val interest_income: String,
    val main_product_service_group: String,
    val name_of_company: String,
    val nse_symbol_bse_script_id: String,
    val status: Int
)