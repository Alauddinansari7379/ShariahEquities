package com.example.tlismimoti.listing.model

data class Data(
    val content: Content,
    val info: Info,
    val next: Next,
    val previous: Previous,
    val sch: List<Sch>,
    val variations: List<Any>,
    val count:String

)