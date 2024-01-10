package com.amtech.gadgetzone.myOrder.activity

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.amtech.gadgetzone.R
import com.amtech.gadgetzone.databinding.FragmentCompleteBinding


class CompleteFragment : Fragment() {
    private lateinit var binding:FragmentCompleteBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
         return inflater.inflate(R.layout.fragment_complete, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding= FragmentCompleteBinding.bind(view)
    }

 }