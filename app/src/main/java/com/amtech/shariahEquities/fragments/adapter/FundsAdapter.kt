package com.amtech.shariahEquities.fragments.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.amtech.shariahEquities.modelCompany.Result
import com.amtech.shariahEquities.profile.activity.PrivacyPolicy
import com.amtech.shariahEquities.sharedpreferences.SessionManager
import com.sellacha.tlismiherbs.databinding.FundslistItemBinding
import com.sellacha.tlismiherbs.databinding.ItemStockBinding


class FundsAdapter(
    val context: Context,
    var list: ArrayList<Result>,
) : RecyclerView.Adapter<FundsAdapter.ViewHolder>() {
    lateinit var sessionManager: SessionManager

    inner class ViewHolder(val binding: FundslistItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            FundslistItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        sessionManager = SessionManager(context)

        with(holder) {
            with(list[position]) {
                try {
                    binding.apply {
                        companyName.text = name_of_company
                        tvSymbol.text = symbol
                        btnAddWatchList.setOnClickListener {
                            val intent = Intent(context as Activity, PrivacyPolicy::class.java)
                                .putExtra("title", "Contact Us")
                                .putExtra("link", "https://forms.gle/6gGfyb4WiLTqZg686")
                            (context as Activity).startActivity(intent)


                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
}

//class FundsAdapter(
//  val context: Context, private val onItemChecked: (Result, Boolean) -> Unit
//) : ListAdapter<Result, FundsAdapter.StockViewHolder>(DiffCallback()) {
//
//    inner class StockViewHolder(private val binding: FundslistItemBinding) : RecyclerView.ViewHolder(binding.root) {
//        fun bind(result: Result) {
//            binding.apply {
//                companyName.text = result.name_of_company
//                symbol.text = result.symbol
//                btnAddWatchList.setOnClickListener {
//                    val intent = Intent(context as Activity, PrivacyPolicy::class.java)
//                        .putExtra("title", "Contact Us")
//                        .putExtra("link", "https://forms.gle/6gGfyb4WiLTqZg686")
//                    (context as Activity).startActivity(intent)
//
//                 }
//            }
//        }
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StockViewHolder {
//        val binding = FundslistItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
//        return StockViewHolder(binding)
//    }
//
//    override fun onBindViewHolder(holder: StockViewHolder, position: Int) {
//        holder.bind(getItem(position))
//    }
//
//    private class DiffCallback : DiffUtil.ItemCallback<Result>() {
//        override fun areItemsTheSame(oldItem: Result, newItem: Result): Boolean {
//            return oldItem.id == newItem.id
//        }
//
//        override fun areContentsTheSame(oldItem: Result, newItem: Result): Boolean {
//            return oldItem == newItem
//        }
//    }
//
//}
