package com.amtech.gadgetzone.myOrder.activity

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.amtech.gadgetzone.R
import com.amtech.gadgetzone.databinding.FragmentProgressBinding


class ProgressFragment : Fragment() {
    private lateinit var binding:FragmentProgressBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
         return inflater.inflate(R.layout.fragment_progress, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding= FragmentProgressBinding.bind(view)

        with(binding){
            card1.setOnClickListener {
                startActivity(Intent(requireContext(),OrderDetails::class.java))
            }

            card2.setOnClickListener {
                startActivity(Intent(requireContext(),OrderDetails::class.java))
            }
        }

    }

 }