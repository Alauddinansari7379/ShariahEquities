package com.amtech.shariahEquities.Search.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.amtech.shariahEquities.fragments.adapter.FundsAdapter.AddWatchList
import com.amtech.shariahEquities.modelCompany.Result
import com.sellacha.tlismiherbs.databinding.ItemStockBinding
class AdapterSearch(
    val context: Context, private val onItemChecked: (Result, Boolean) -> Unit, val addWatchList: AddWatchList
) : ListAdapter<Result, AdapterSearch.StockViewHolder>(DiffCallback()) {

    private var showCheckboxes = false
    private val selectedItems: MutableMap<Long, Boolean> = mutableMapOf()

    inner class StockViewHolder(val binding: ItemStockBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(result: Result) {
            binding.apply {
                companyName.text = result.name_of_company
                companySymbol.text = result.symbol
             if (result.complaint_type==1){
                 complianceTag.visibility=View.VISIBLE
                 nonComplianceTag.visibility=View.GONE
             }else{
                 nonComplianceTag.visibility=View.VISIBLE
                 complianceTag.visibility=View.GONE

             }

                checkbox.setOnCheckedChangeListener(null)
                checkbox.isChecked = selectedItems[result.id.toLong()] ?: false
                checkbox.visibility = if (showCheckboxes) View.VISIBLE else View.GONE
                checkbox.setOnCheckedChangeListener { _, isChecked ->
                    selectedItems[result.id.toLong()] = isChecked
                    onItemChecked(result, isChecked)
                }

                btnAddWatchList.setOnClickListener {
                    addWatchList.addWatchList(result.id.toString())
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StockViewHolder {
        val binding = ItemStockBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StockViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StockViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    fun setShowCheckboxes(show: Boolean) {
        showCheckboxes = show
        notifyDataSetChanged()
    }

    fun getSelectedItems(): List<Result> {
        return currentList.filter { selectedItems[it.id.toLong()] == true }
    }
    fun clearSelectedItems() {
        selectedItems.clear()
        notifyDataSetChanged()
    }

    private class DiffCallback : DiffUtil.ItemCallback<Result>() {//refresh the changed data
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

