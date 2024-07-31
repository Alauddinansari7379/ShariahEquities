package com.amtech.tlismiherbs.home.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sellacha.tlismiherbs.R
import com.amtech.tlismiherbs.category.Listing


class AdapterCategory(val context: Context, private val list: List<String>) :
    RecyclerView.Adapter<AdapterCategory.MyViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            LayoutInflater.from(context).inflate(R.layout.single_row_catgory, parent, false)
        )
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        // holder.SrNo.text= "${position+1}"
        try {
             holder.tvCatRe.text = list[position].toString()

           // holder.tvPricePro.text = "₹" + list[position].price.price.toString()
            // holder.tvRatingPro.text = list[position].title
//            if (list[position].preview != null) {
//                Picasso.get().load("https:" + list[position].preview.media.url)
//                    .placeholder(R.drawable.placeholder_n)
//                    .error(R.drawable.error_placeholder)
//                    .into(holder.imgPro)
//
//        }
        holder.itemView.setOnClickListener {
            val intent = Intent(context as Activity, Listing::class.java)
                .putExtra("value", list[position])
            (context as Activity).startActivity(intent)
            }
//            val intent = Intent(context as Activity, DetailPage::class.java)
//                .putExtra("id", list[position].id.toString())
//                .putExtra("value", category)
//            context.startActivity(intent)
//        }
    }catch (e:Exception){
        e.printStackTrace()
    }
        }


    override fun getItemCount(): Int {
        return list.size

    }

    open class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
         val tvCatRe: TextView = itemView.findViewById(R.id.tvCatRe)



    }
}