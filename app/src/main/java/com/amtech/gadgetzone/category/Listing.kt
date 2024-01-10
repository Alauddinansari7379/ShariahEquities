package com.amtech.gadgetzone.category

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.amtech.gadgetzone.databinding.ActivityListingBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class Listing : AppCompatActivity() {
    private val binding by lazy {
        ActivityListingBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        with(binding){
            filter.setOnClickListener {
                val bottomSheetFragment = BottomFilterFragment()
                bottomSheetFragment.show(supportFragmentManager, bottomSheetFragment.tag)
            }
        }

    }
}