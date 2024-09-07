package com.amtech.shariahEquities.notification.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.amtech.shariahEquities.notification.modelwatchlist.Result
import com.sellacha.tlismiherbs.databinding.WatchlistItemBinding

class WatchListAdapter(
    private val context: Context,
    private val watchList: ArrayList<Result>
) : RecyclerView.Adapter<WatchListAdapter.MyViewHolder>() {
    class MyViewHolder(val binding: WatchlistItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = WatchlistItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = watchList[position]


        with(holder) {

            with(binding) {
                tvCompanyName.text = item.name_of_company
                symbol.text = item.symbol
                itemView.setOnClickListener {

                }
            }
        }
    }

    override fun getItemCount(): Int {
        return watchList.size
    }
}
