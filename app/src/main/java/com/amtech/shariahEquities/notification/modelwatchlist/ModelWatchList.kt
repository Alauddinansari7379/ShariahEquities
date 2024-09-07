package com.amtech.shariahEquities.notification.modelwatchlist

data class ModelWatchList(
    val message: String,
    val result: List<Result>,
    val status: Int
)