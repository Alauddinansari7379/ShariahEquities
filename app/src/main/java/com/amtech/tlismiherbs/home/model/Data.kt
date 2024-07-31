package com.example.tlismimoti.home.model

data class Data(
    val actives: Int,
    val drafts: Int,
    val incomplete: Int,
    val posts: Posts,
    val request: Request,
    val src: String,
    val trash: Int,
    val type: Int
)