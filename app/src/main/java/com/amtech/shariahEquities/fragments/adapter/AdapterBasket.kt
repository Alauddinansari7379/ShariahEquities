package com.amtech.shariahEquities.fragments.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.amtech.shariahEquities.fragments.model.modelGetBasket.Result
import com.amtech.shariahEquities.sharedpreferences.SessionManager
import com.sellacha.tlismiherbs.databinding.SingleRowBasketListBinding

import com.squareup.picasso.Picasso

class AdapterBasket(
    val context: Context,
    var list: ArrayList<Result>,
 ) : RecyclerView.Adapter<AdapterBasket.ViewHolder>() {
    lateinit var sessionManager: SessionManager

    inner class ViewHolder(val binding: SingleRowBasketListBinding) : RecyclerView.ViewHolder(binding.root)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = SingleRowBasketListBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }
    override fun getItemCount(): Int {
        return list.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        sessionManager= SessionManager(context)

        try {
            with(holder){
                with(list[position]){
                    binding.tvBasketName.text = basketname
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}
