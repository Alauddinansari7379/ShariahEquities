package com.amtech.gadgetzone.myOrder.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.amtech.gadgetzone.R
import com.amtech.gadgetzone.databinding.ActivityOrderDetailsBinding

class OrderDetails : AppCompatActivity() {
    private val binding by lazy{
        ActivityOrderDetailsBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

    }
}