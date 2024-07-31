package com.amtech.tlismiherbs.myOrder.activity

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.amtech.tlismiherbs.Helper.AppProgressBar
 import com.sellacha.tlismiherbs.R
import com.sellacha.tlismiherbs.databinding.FragmentProgressBinding
import com.amtech.tlismiherbs.myOrder.adapter.AdapterPendingOrder
import com.amtech.tlismiherbs.retrofit.ApiClient
import com.amtech.tlismiherbs.sharedpreferences.SessionManager
import com.example.tlismimoti.Helper.myToast
 import com.sellacha.tlismiherbs.myOrder.activity.model.InfoX
import com.example.tlismimoti.order.model.ModelOrder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProgressFragment : Fragment() {
    private lateinit var binding: FragmentProgressBinding
    lateinit var sessionManager: SessionManager
    var count = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_progress, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentProgressBinding.bind(view)
        sessionManager = SessionManager(requireContext())
//        binding.cardProgress.setOnClickListener {
//            // startActivity(Intent(this,Order))
//        }
        if (sessionManager.userEmail!!.isNotEmpty()){
            apiCallAllOrder()

        }
    }

    private fun apiCallAllOrder() {
        // AppProgressBar.showLoaderDialog(requireContext())
        ApiClient.apiService.orders(sessionManager.authToken!!, sessionManager.userEmail,"pending")
            .enqueue(object : Callback<ModelOrder> {
                override fun onResponse(
                    call: Call<ModelOrder>, response: Response<ModelOrder>
                ) {
                    try {
                        if (response.code() == 500) {
                            myToast(requireActivity(), "Server Error")
                            AppProgressBar.hideLoaderDialog()

                        } else if (response.code() == 404) {
                            myToast(requireActivity(), "Something went wrong")
                            AppProgressBar.hideLoaderDialog()

                        } else if (response.body()!!.data.info.isEmpty()) {
                             binding.tvNoDtaFound.visibility = View.VISIBLE
                            //   binding.shimmer.visibility = View.GONE
                            AppProgressBar.hideLoaderDialog()

                        } else {
                            val totalAmt=ArrayList<Int>()
                            val completed = ArrayList<InfoX>()
                            val pending = ArrayList<InfoX>()
                            val canceled = ArrayList<InfoX>()
                            // mainData = response.body()!!.data.posts.data
                            for (i in response.body()!!.data.info) {
                                when (i.status) {
                                    "completed" -> completed.add(i)
                                    "1" -> {
                                        totalAmt.add(i.amount)


                                        pending.add(i)
                                    }
                                    "canceled" -> canceled.add(i)
                                }
                                // i.order_content.value.find {  }
                            }

                            Log.e("completed", completed.toString())
                            Log.e("pending", pending.toString())
                            Log.e("canceled", canceled.toString())
//                            binding.itemQty.text=pending.size.toString()+" items"
//                            binding.totalPrice.text="₹"+totalAmt.sum()
                            binding!!.recyclerView.adapter =
                                activity?.let { AdapterPendingOrder(it, pending,) }


                            AppProgressBar.hideLoaderDialog()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onFailure(call: Call<ModelOrder>, t: Throwable) {
                    //myToast(requireActivity(), "Something went wrong")
                    count++
                    if (count <= 2) {
                        Log.e("count", count.toString())
                        apiCallAllOrder()
                    } else {
                        myToast(requireActivity(), t.message.toString())
                        AppProgressBar.hideLoaderDialog()

                    }
                    AppProgressBar.hideLoaderDialog()


                }

            })
    }


}