package com.amtech.tlismiherbs.home.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sellacha.tlismiherbs.R
import com.amtech.tlismiherbs.category.Details
import com.sellacha.tlismiherbs.home.model.DataX
import com.squareup.picasso.Picasso


class AdapterProduct(val context: Context, private val list: ArrayList<DataX>) :
    RecyclerView.Adapter<AdapterProduct.MyViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            LayoutInflater.from(context).inflate(R.layout.single_row_product, parent, false)
        )
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        // holder.SrNo.text= "${position+1}"
        try {


            holder.tvTitle.text = list[position].title
            holder.tvPricePro.text = "₹" + list[position].price.price.toString()
            // holder.tvRatingPro.text = list[position].title


            if (list[position].medias != null) {
                for (i in list[position].medias) {
                    val imageUrl = "https:" + i.url
                    Log.d("ImageURL", "Loading image from URL: $imageUrl")
                    Picasso.get().load(imageUrl)
                        .placeholder(R.drawable.placeholder_n)
                        .error(R.drawable.error_placeholder)
                        .into(holder.imgPro, object : com.squareup.picasso.Callback {
                            override fun onSuccess() {
                                Log.d("Picasso", "Image loaded successfully.")
                            }

                            override fun onError(e: Exception) {
                                Log.e("Picasso", "Error loading image", e)
                            }
                        })
                }
            }


            holder.itemView.setOnClickListener {
                var category = ""
                for (i in list[position].categories) {
                    category = i.name

                }
                if (list[position].affiliate != null) {
                    val intent =
                        Intent(Intent.ACTION_VIEW, Uri.parse(list[position].affiliate.value))
                    context.startActivity(Intent.createChooser(intent, "Choose browser"))
                } else {
                    val intent = Intent(context as Activity, Details::class.java)
                        .putExtra("id", list[position].id.toString())
                        .putExtra("value", category)
                    context.startActivity(intent)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    override fun getItemCount(): Int {
        return list.size

    }

    open class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgPro: ImageView = itemView.findViewById(R.id.imgPro)
        val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        val tvPricePro: TextView = itemView.findViewById(R.id.tvPricePro)
        val tvRatingPro: TextView = itemView.findViewById(R.id.tvRatingPro)
        val tvDescPro: TextView = itemView.findViewById(R.id.tvDescPro)


    }
}