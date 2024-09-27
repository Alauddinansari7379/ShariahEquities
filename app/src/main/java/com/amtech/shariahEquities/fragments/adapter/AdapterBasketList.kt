package com.amtech.shariahEquities.fragments.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.amtech.shariahEquities.fragments.model.modelBasketList.Company
import com.amtech.shariahEquities.fragments.model.modelGetBasket.Result
import com.amtech.shariahEquities.sharedpreferences.SessionManager
import com.sellacha.tlismiherbs.databinding.ItemStockBinding
import com.sellacha.tlismiherbs.databinding.SingleRowBasketListBinding
import org.json.JSONArray
import retrofit2.http.DELETE

class AdapterBasketList(
    val context: Context,
    var list: ArrayList<Company>,val delete: Delete
 ) : RecyclerView.Adapter<AdapterBasketList.ViewHolder>() {
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
        sessionManager= SessionManager(context)
        try {
            with(holder) {
                with(list[position]) {
                    binding.btnAddWatchList.visibility= View.GONE
                    binding.companySymbol.text = nse_symbol_bse_script_id
                    binding.tvExchange.text = exchange
                    if (final == "PASS") {
                        binding.complianceTag.visibility = View.VISIBLE
                        binding.nonComplianceTag.visibility = View.GONE
                    } else {
                        binding.nonComplianceTag.visibility = View.VISIBLE
                        binding.complianceTag.visibility = View.GONE

                    }
                    binding.imgDelete.setOnClickListener {
                        delete.delete(id.toString())
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
interface Delete {
    fun delete(id:String)
}

}
