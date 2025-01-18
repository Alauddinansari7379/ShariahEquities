package com.amtech.shariahEquities.notification.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.amtech.shariahEquities.fragments.ComplianceReportActivity
import com.amtech.shariahEquities.notification.modelwatchlist.Result
import com.amtech.shariahEquities.sharedpreferences.SessionManager
import com.sellacha.tlismiherbs.databinding.WatchlistItemBinding
import retrofit2.http.DELETE

class WatchListAdapter(val context: Context,
    private val watchList: ArrayList<Result>, val delete: Delete,
) : RecyclerView.Adapter<WatchListAdapter.MyViewHolder>() {

    private lateinit var sessionManager: SessionManager

    class MyViewHolder(val binding: WatchlistItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = WatchlistItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        sessionManager = SessionManager(parent.context)
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
                companySymbol.text = item.nse_symbol_bse_script_id

//                if (sessionManager.subscribed.toString() != "0") {
                    if (item.final == "PASS" && item.financial_screening == "PASS") {
                        binding.complianceTag.visibility = View.VISIBLE
                        binding.nonComplianceTag.visibility = View.GONE
                    } else if (item.final == "PASS" && item.financial_screening == "FAIL") {
                        binding.nonComplianceTag.visibility = View.VISIBLE
                        binding.complianceTag.visibility = View.GONE
                    } else {
                        binding.nonComplianceTag.visibility = View.VISIBLE
                        binding.complianceTag.visibility = View.GONE

                    }
//                }
                binding.root.setOnClickListener {
                    val intent = Intent(context, ComplianceReportActivity::class.java)
                    intent.putExtra("id", item.company_id.toInt())
                    context.startActivity(intent)
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
        fun delete(id: String)
    }
}
