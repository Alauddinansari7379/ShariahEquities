package com.amtech.shariahEquities.profile.activity.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.amtech.shariahEquities.fragments.BasketsList
import com.amtech.shariahEquities.profile.activity.model.Result
import com.amtech.shariahEquities.sharedpreferences.SessionManager
import com.sellacha.tlismiherbs.databinding.SingleRowBasketListBinding
import com.sellacha.tlismiherbs.databinding.SingleRowTransListBinding
import org.json.JSONArray

class AdapterTransList(
    val context: Context,
    var list: MutableList<Result>, val download: Download
) : RecyclerView.Adapter<AdapterTransList.ViewHolder>() {
    lateinit var sessionManager: SessionManager

    inner class ViewHolder(val binding: SingleRowTransListBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            SingleRowTransListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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
                    binding.tvTransId.text = transaction_id
                    binding.tvPaymentStatus.text = paymentStatus
                    binding.tvPaymentDate.text = paymentDate
                    binding.tvMethodTransactionId.text = methodTransactionId.toString()


 //                    binding.btnView.setOnClickListener {
//                        val intent = Intent(context as Activity, BasketsList::class.java)
//                            .putExtra("basketId", id.toString())
//                        context.startActivity(intent)
//                    }

                    binding.imgDownload.setOnClickListener {
                       // delete.delete(id.toString())
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    interface Download {
        fun download(id: String)
    }

}
