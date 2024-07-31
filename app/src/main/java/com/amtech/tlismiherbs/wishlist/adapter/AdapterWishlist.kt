package com.amtech.tlismiherbs.wishlist.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sellacha.tlismiherbs.R
import com.amtech.tlismiherbs.category.Details
import com.sellacha.tlismiherbs.databinding.SingleRowCartBinding
import com.amtech.tlismiherbs.sharedpreferences.SessionManager
import com.example.tlismimoti.Helper.vibrateOnce
import com.example.tlismimoti.wishlist.model.Item
import com.squareup.picasso.Picasso

class AdapterWishlist(
    val context: Context,
    var list: ArrayList<Item>,
 ) : RecyclerView.Adapter<AdapterWishlist.ViewHolder>() {
    lateinit var sessionManager: SessionManager

    inner class ViewHolder(val binding: SingleRowCartBinding) : RecyclerView.ViewHolder(binding.root)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = SingleRowCartBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }
    override fun getItemCount(): Int {
        return list.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        sessionManager= SessionManager(context)
        with(holder){
            with(list[position]){
                binding.layoutAdd.visibility= View.GONE
                binding.tvQty.visibility= View.GONE
                // binding.priceUndrline.paintFlags = binding.priceUndrline.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                binding.price.text = "₹$price"
                binding.title.text = term_title
              //  binding.qty.text = qty

                if (list[position].preview != null) {
                    Picasso.get().load("https:"+list[position].preview)
                        .placeholder(R.drawable.placeholder_n)
                        .error(R.drawable.error_placeholder)
                        .into(binding.image)
                }

//                for (i in list[position].medias){
//                    if (list[position].medias != null) {
//                        Picasso.get().load("https:" + i.url)
//                            .placeholder(R.drawable.placeholder_n)
//                            .error(R.drawable.error_placeholder)
//                            .into(holder.imgPro)
//
//                    }
//                }
                itemView.setOnClickListener {
                    val intent = Intent(context as Activity, Details::class.java)
                        .putExtra("id", term_id)
                    context.startActivity(intent)
                }
//                binding.layoutPlush.setOnClickListener {
//                    vibrateOnce(context)
//                    cart.addToCart(term_id)
//                }
                binding.layoutDelete.setOnClickListener {
                    vibrateOnce(context)
                  //  cart.removeToCart(id)
                }

            }
        }
    }

}
