package com.amtech.tlismiherbs.profile.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.sellacha.tlismiherbs.R
import com.sellacha.tlismiherbs.databinding.ActivityPromoBinding

class Promo : AppCompatActivity() {
val binding by lazy { ActivityPromoBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        with(binding){
            imgBack.setOnClickListener {
                onBackPressed()
            }
        }
    }
}