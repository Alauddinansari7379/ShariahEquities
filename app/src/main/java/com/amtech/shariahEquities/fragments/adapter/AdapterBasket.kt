package com.amtech.shariahEquities.fragments.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.amtech.shariahEquities.fragments.BasketsList
import com.amtech.shariahEquities.fragments.model.modelGetBasket.Result
import com.amtech.shariahEquities.sharedpreferences.SessionManager
import com.sellacha.tlismiherbs.databinding.SingleRowBasketListBinding
import org.json.JSONArray
import retrofit2.http.DELETE

class AdapterBasket(
    val context: Context,
    var list: ArrayList<Result>, val delete: Delete
) : RecyclerView.Adapter<AdapterBasket.ViewHolder>() {
    lateinit var sessionManager: SessionManager

    inner class ViewHolder(val binding: SingleRowBasketListBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            SingleRowBasketListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        sessionManager = SessionManager(context)
        try {
            with(holder) {
                with(list[position]) {
                    binding.tvBasketName.text = basketname
                    binding.tvDescription.text = description
                    val companyidString = companyid
                    val outerArray = JSONArray(companyidString)
                    val innerJsonString = outerArray.getString(0)
                    val innerArray = JSONArray(innerJsonString)
                    val companyIds = mutableListOf<String>()
                    for (i in 0 until innerArray.length()) {
                        val id = innerArray.getString(i)
                        companyIds.add(id)
                    }
                    binding.tvBasketQty.text = "Basket Qty :" + companyIds.size.toString()
                    binding.root.setOnClickListener {
                        val intent = Intent(context as Activity, BasketsList::class.java)
                            .putExtra("basketId", id.toString())
                        context.startActivity(intent)
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
        fun delete(id: String)
    }

}
