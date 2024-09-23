package com.amtech.shariahEquities.fragments.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.amtech.shariahEquities.fragments.ComplianceReportActivity
import com.amtech.shariahEquities.modelCompany.Result
import com.amtech.shariahEquities.sharedpreferences.SessionManager
import com.sellacha.tlismiherbs.databinding.ItemStockBinding

class StocksAdapter(
    val context: Context,
    var list: List<Result>,
) : RecyclerView.Adapter<StocksAdapter.ViewHolder>() {
    lateinit var sessionManager: SessionManager

    inner class ViewHolder(val binding: ItemStockBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemStockBinding.inflate(LayoutInflater.from(parent.context), parent, false)

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
                binding.btnAddWatchList.visibility=View.GONE
                binding.imgDelete.visibility=View.GONE
                binding.companyName.text = name_of_company
                binding.companySymbol.text = nse_symbol_bse_script_id
                if (complaint_type == 1) {
                    binding.complianceTag.visibility = View.VISIBLE
                    binding.nonComplianceTag.visibility = View.GONE
                } else {
                    binding.nonComplianceTag.visibility = View.VISIBLE
                    binding.complianceTag.visibility = View.GONE

                }
//                if (sessionManager.subscribed == "0") {
//                     binding.nonComplianceTagBluer.visibility = View.VISIBLE
//                    binding.nonComplianceTag.visibility = View.GONE
//                    binding.complianceTag.visibility = View.GONE
//                }
                binding.root.setOnClickListener {
                    val intent = Intent(context, ComplianceReportActivity::class.java)
                    intent.putExtra("id", id)
                    context.startActivity(intent)
                }
            }
        }
    }
}
