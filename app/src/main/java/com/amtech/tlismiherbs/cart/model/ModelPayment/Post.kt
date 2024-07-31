package com.example.tlismimoti.cart.model.ModelPayment

data class Post(
    val active_getway: ActiveGetway,
    val created_at: String,
    val description: Description,
    val featured: Int,
    val id: Int,
    val is_admin: Int,
    val menu_status: Int,
    val name: String,
    val p_id: Any,
    val s_id: Any,
    val slug: String,
    val type: String,
    val updated_at: String,
    val user_id: Int
)