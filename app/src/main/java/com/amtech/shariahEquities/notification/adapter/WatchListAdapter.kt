package com.amtech.shariahEquities.notification.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.amtech.shariahEquities.notification.modelwatchlist.Result
import com.sellacha.tlismiherbs.databinding.WatchlistItemBinding
import retrofit2.http.DELETE

class WatchListAdapter(
    private val watchList: ArrayList<Result>, val delete: Delete,
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

                imgDelete.setOnClickListener {
                    delete.delete(item.id.toString())
                }
                companyName.text = item.name_of_company
                companySymbol.text = item.symbol
                if (item.complaint_type == 1) {
                    complianceTag.visibility = View.VISIBLE
                    nonComplianceTag.visibility = View.GONE
                } else {
                    nonComplianceTag.visibility = View.VISIBLE
                    complianceTag.visibility = View.GONE

                }
            }
        }
    }

    private class DiffCallback :
        DiffUtil.ItemCallback<com.amtech.shariahEquities.modelCompany.Result>() {
        override fun areItemsTheSame(
            oldItem: com.amtech.shariahEquities.modelCompany.Result,
            newItem: com.amtech.shariahEquities.modelCompany.Result
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: com.amtech.shariahEquities.modelCompany.Result,
            newItem: com.amtech.shariahEquities.modelCompany.Result
        ): Boolean {
            return oldItem == newItem
        }
    }

    override fun getItemCount(): Int {
        return watchList.size
    }

    interface Delete {
        fun delete(id:String)
    }
}
