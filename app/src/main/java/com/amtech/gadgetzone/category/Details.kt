package com.amtech.gadgetzone.category

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.amtech.gadgetzone.R
import com.amtech.gadgetzone.databinding.ActivityDetailsBinding

class Details : AppCompatActivity() {
    private val binding by lazy {
        ActivityDetailsBinding.inflate(layoutInflater)
    }

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