package com.amtech.gadgetzone.profile.activity

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.amtech.gadgetzone.R
import com.amtech.gadgetzone.databinding.FragmentProfileBinding
import com.amtech.gadgetzone.myOrder.activity.MyOrder

class ProfileFragment : Fragment() {
    private lateinit var binding:FragmentProfileBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding=FragmentProfileBinding.bind(view)

        with(binding){
            cardPromo.setOnClickListener {
                startActivity(Intent(requireContext(),Promo::class.java))
            }
            cardMyOrder.setOnClickListener {
                startActivity(Intent(requireContext(),MyOrder::class.java))
            }

            cardRefer.setOnClickListener {
                startActivity(Intent(requireContext(),ReferAndEarn::class.java))
            }
        }

    }

 }