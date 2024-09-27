package com.amtech.shariahEquities.Search.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import cn.pedant.SweetAlert.SweetAlertDialog
import com.amtech.shariahEquities.fragments.ComplianceReportActivity
import com.amtech.shariahEquities.login.Login
import com.amtech.shariahEquities.modelCompany.Result
import com.amtech.shariahEquities.sharedpreferences.SessionManager
import com.example.tlismimoti.Helper.myToast
import com.sellacha.tlismiherbs.databinding.ItemStockBinding
import com.sellacha.tlismiherbs.databinding.SingleRowSearchBinding

class AdapterSearch(
    val context: Context,
    private val onItemChecked: (Result, Boolean) -> Unit,
    val addWatchList: AddWatchList
) : ListAdapter<Result, AdapterSearch.StockViewHolder>(DiffCallback()) {

    private var showCheckboxes = false
    private val selectedItems: MutableMap<Long, Boolean> = mutableMapOf()
    lateinit var sessionManager: SessionManager

    inner class StockViewHolder(val binding: SingleRowSearchBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(result: Result) {
            sessionManager = SessionManager(context)
            binding.apply {
                companyName.text = result.name_of_company
                companySymbol.text = result.nse_symbol_bse_script_id
                if (result.final == "PASS") {
                    binding.complianceTag.visibility = View.VISIBLE
                    binding.nonComplianceTag.visibility = View.GONE
                } else {
                    binding.nonComplianceTag.visibility = View.VISIBLE
                    binding.complianceTag.visibility = View.GONE

                }

                checkbox.setOnCheckedChangeListener(null)
                checkbox.isChecked = selectedItems[result.id.toLong()] ?: false
                checkbox.visibility = if (showCheckboxes) View.VISIBLE else View.GONE
                checkbox.setOnCheckedChangeListener { _, isChecked ->
                    selectedItems[result.id.toLong()] = isChecked
                    onItemChecked(result, isChecked)
                }
//                if (sessionManager.subscribed == "0") {
//                    binding.imgLock.visibility = View.VISIBLE
//                    binding.nonComplianceTagBluer.visibility = View.VISIBLE
//                    nonComplianceTag.visibility = View.GONE
//                    complianceTag.visibility = View.GONE
//                }else{
//                    binding.imgLock.visibility = View.GONE
//                }
                btnAddWatchList.setOnClickListener {
//                    if (sessionManager.subscribed == "0") {
//                        SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
//                            .setTitleText("Please upgrade the plan.")
//                             .setConfirmText("ok")
//                            .showCancelButton(true)
//                            .setConfirmClickListener { sDialog ->
//                                sDialog.cancel()
//                            }
//                            .show()
//                    } else {
                        addWatchList.addWatchList(result.id.toString())
//                    }
                }
                binding.root.setOnClickListener {
                    val intent = Intent(context, ComplianceReportActivity::class.java)
                    intent.putExtra("id", result.id)
                    context.startActivity(intent)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StockViewHolder {
        val binding =
            SingleRowSearchBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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

    private class DiffCallback : DiffUtil.ItemCallback<Result>() {
        //refresh the changed data
        override fun areItemsTheSame(oldItem: Result, newItem: Result): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Result, newItem: Result): Boolean {
            return oldItem == newItem
        }
    }

    interface AddWatchList {
        fun addWatchList(compenyId: String)
    }
}

