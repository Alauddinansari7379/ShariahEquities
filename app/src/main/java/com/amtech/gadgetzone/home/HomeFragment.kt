package com.amtech.gadgetzone.home

import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.amtech.gadgetzone.R
import com.amtech.gadgetzone.category.Category
import com.amtech.gadgetzone.category.Details
import com.amtech.gadgetzone.category.Listing
import com.amtech.gadgetzone.databinding.FragmentHomeBinding


class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHomeBinding.bind(view)

        with(binding){
            tvViewAllCate.setOnClickListener {
                startActivity(Intent(requireContext(),Category::class.java))
            }
            tvToTrending.setOnClickListener {
                startActivity(Intent(requireContext(),Listing::class.java))
            }
            tvDisc.setOnClickListener {
                startActivity(Intent(requireContext(),Listing::class.java))
            }
            tvBestSale.setOnClickListener {
                startActivity(Intent(requireContext(),Listing::class.java))
            }

            item.setOnClickListener {
                startActivity(Intent(requireContext(),Details::class.java))
            }
            priceUndrline.paintFlags =priceUndrline.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG

        }


    }

}