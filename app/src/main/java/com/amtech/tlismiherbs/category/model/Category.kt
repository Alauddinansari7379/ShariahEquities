package com.example.tlismimoti.listing.model

data class Category(
    val created_at: String,
    val featured: Int,
    val id: Int,
    val is_admin: Int,
    val menu_status: Int,
    val name: String,
    val p_id: Int,
    val pivot: Pivot,
    val slug: String,
    val type: String,
    val updated_at: String,
    val user_id: Int
)