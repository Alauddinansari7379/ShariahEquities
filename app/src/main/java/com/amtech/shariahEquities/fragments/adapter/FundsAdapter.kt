package com.amtech.shariahEquities.fragments.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.amtech.shariahEquities.modelCompany.Result
import com.amtech.shariahEquities.profile.activity.PrivacyPolicy
import com.sellacha.tlismiherbs.databinding.FundslistItemBinding

class FundsAdapter(
  val context: Context, private val onItemChecked: (Result, Boolean) -> Unit,val addWatchList: AddWatchList
) : ListAdapter<Result, FundsAdapter.StockViewHolder>(DiffCallback()) {

    inner class StockViewHolder(private val binding: FundslistItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(result: Result) {
            binding.apply {
                companyName.text = result.name_of_company
                symbol.text = result.symbol
                btnAddWatchList.setOnClickListener {
                    val intent = Intent(context as Activity, PrivacyPolicy::class.java)
                        .putExtra("title", "Contact Us")
                        .putExtra("link", "https://forms.gle/6gGfyb4WiLTqZg686")
                    (context as Activity).startActivity(intent)

                 }



            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StockViewHolder {
        val binding = FundslistItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StockViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StockViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    private class DiffCallback : DiffUtil.ItemCallback<Result>() {
        override fun areItemsTheSame(oldItem: Result, newItem: Result): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Result, newItem: Result): Boolean {
            return oldItem == newItem
        }
    }
    interface AddWatchList{
        fun addWatchList(compenyId:String)
    }
}
