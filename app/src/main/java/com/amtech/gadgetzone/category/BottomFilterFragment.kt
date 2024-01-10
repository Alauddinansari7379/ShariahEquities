package com.amtech.gadgetzone.category

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.amtech.gadgetzone.R
import com.amtech.gadgetzone.databinding.FragmentBottomFilterBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class BottomFilterFragment : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentBottomFilterBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_bottom_filter, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding= FragmentBottomFilterBinding.bind(view)

        with(binding){
            layMen.setOnClickListener {
                layMen.setBackgroundResource(R.drawable.corner_filter)
                tvMale.setTextColor(Color.parseColor("#FFFFFF"))
                tvFemale.setTextColor(Color.parseColor("#777777"))
                tvOther.setTextColor(Color.parseColor("#777777"))
                layWomen.setBackgroundResource(0)
                layOther.setBackgroundResource(0)


            }
            layWomen.setOnClickListener {
                layWomen.setBackgroundResource(R.drawable.corner_filter)
                tvFemale.setTextColor(Color.parseColor("#FFFFFF"))
                tvOther.setTextColor(Color.parseColor("#777777"))
                tvMale.setTextColor(Color.parseColor("#777777"))
                layMen.setBackgroundResource(0)
                layOther.setBackgroundResource(0)


            }
            layOther.setOnClickListener {
                layOther.setBackgroundResource(R.drawable.corner_filter)
                tvOther.setTextColor(Color.parseColor("#FFFFFF"))
                tvFemale.setTextColor(Color.parseColor("#777777"))
                tvMale.setTextColor(Color.parseColor("#777777"))
                layMen.setBackgroundResource(0)
                layWomen.setBackgroundResource(0)
            }

            imgClose.setOnClickListener {
                dismiss()
            }

        }

    }

}