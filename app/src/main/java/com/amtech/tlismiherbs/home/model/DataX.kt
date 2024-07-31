package com.sellacha.tlismiherbs.home.model

import com.example.tlismimoti.home.model.Affiliate
import com.example.tlismimoti.home.model.Content
import com.example.tlismimoti.home.model.Media
import com.example.tlismimoti.home.model.Preview
import com.example.tlismimoti.home.model.Price
import com.example.tlismimoti.home.model.Seo
import com.example.tlismimoti.home.model.Stock


data class DataX(
    val affiliate: Affiliate,
    val answer1: Any,
    val answer2: Any,
    val answer3: Any,
    val answer4: Any,
    val answer5: Any,
    val brands: List<Any>,
    val categories: ArrayList<Category>,
    val content: Content,
    val created_at: String,
    val featured: Int,
    val formate_date: String,
    val id: Int,
    val is_admin: Int,
    val medias: List<Media>,
    val options: List<Any>,
    val order_count: Int,
    val preview: Preview,
    val price: Price,
    val question1: Any,
    val question2: Any,
    val question3: Any,
    val question4: Any,
    val question5: Any,
    val seo: Seo,
    val service_location: Any,
    val service_type: Any,
    val slug: String,
    val status: Int,
    val stock: Stock,
    val title: String,
    val type: String,
    val updated_at: String,
    val user_id: Int,
    val youtube_link: Any
){

}