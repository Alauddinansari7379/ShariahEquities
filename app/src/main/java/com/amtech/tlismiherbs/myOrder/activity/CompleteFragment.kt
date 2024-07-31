package com.amtech.tlismiherbs.myOrder.activity

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.amtech.tlismiherbs.Helper.AppProgressBar
 import com.sellacha.tlismiherbs.R
import com.sellacha.tlismiherbs.databinding.FragmentCompleteBinding
import com.sellacha.tlismiherbs.myOrder.activity.model.InfoX
import com.amtech.tlismiherbs.myOrder.adapter.AdapterCompletedOrder
import com.amtech.tlismiherbs.retrofit.ApiClient
import com.amtech.tlismiherbs.sharedpreferences.SessionManager
import com.example.tlismimoti.Helper.myToast
import com.example.tlismimoti.order.model.ModelOrder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class CompleteFragment : Fragment() {
    private lateinit var binding: FragmentCompleteBinding
    lateinit var sessionManager: SessionManager
    var count = 0
    var completed = ArrayList<InfoX>()
    var allList = ArrayList<InfoX>()
    val pending = ArrayList<InfoX>()
    var canceled = ArrayList<InfoX>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_complete, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentCompleteBinding.bind(view)

        sessionManager = SessionManager(requireContext())
        if (sessionManager.userEmail!!.isNotEmpty()) {
            apiCallAllOrderCom()
            apiCallAllOrderCan()

        }
    }

    private fun apiCallAllOrderCom() {
        // AppProgressBar.showLoaderDialog(requireContext())
        ApiClient.apiService.orders(
            sessionManager.authToken!!,
            sessionManager.userEmail,
            "completed"
        )
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

                        } else {
                            completed = response.body()!!.data.info as ArrayList<InfoX>
                            val infoList = response.body()?.data?.info ?: listOf()

// Create a new list with the updated status
                            val updatedInfoList = infoList.map { info ->
                                info.copy(status = "Delivered")
                            }
                            allList.addAll(updatedInfoList)


                            Log.e("completed", completed.toString())
                            Log.e("canceled", canceled.toString())
//                            binding.itemQtyDel.text = completed.size.toString() + " items"
//                            binding.totalPrice.text = "₹" + response.body()!!.data.total_amount

//                            binding!!.recyclerView.adapter =
//                                activity?.let {
//                                    AdapterCompletedOrder(
//                                        it, completed,
//                                    )
//                                }

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
                        apiCallAllOrderCom()
                    } else {
                        myToast(requireActivity(), t.message.toString())
                        AppProgressBar.hideLoaderDialog()

                    }
                    AppProgressBar.hideLoaderDialog()


                }

            })
    }

    private fun apiCallAllOrderCan() {
        // AppProgressBar.showLoaderDialog(requireContext())
        ApiClient.apiService.orders(
            sessionManager.authToken!!,
            sessionManager.userEmail,
            "canceled"
        )
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

                        } else {
                            //                             val canceled = ArrayList<Info>()
//                            // mainData = response.body()!!.data.posts.data
//                            for (i in response.body()!!.data.info) {
//                                when (i.status) {
//                                    "canceled" -> canceled.add(i)
//                                }
//                                // i.order_content.value.find {  }
//                            }
                            //   data class InfoX(val someField: String, val status: String)
//
                            // Assuming response.body()?.data?.info is a list of InfoX objects
                            val infoList = response.body()?.data?.info ?: listOf()

// Create a new list with the updated status
                            val updatedInfoList = infoList.map { info ->
                                info.copy(status = "Canceled")
                            }
                            allList.addAll(updatedInfoList)

                            canceled = response.body()!!.data.info as ArrayList<InfoX>
//                            binding.itemQty.text = canceled.size.toString() + " items"
//                            binding.totalPriceCan.text = "₹" + response.body()!!.data.total_amount
                            binding.recyclerView.adapter =
                                activity?.let {
                                    AdapterCompletedOrder(
                                        it, allList,
                                    )
                                }
                            if (canceled.isEmpty() && completed.isEmpty()) {
                                binding.tvNoDtaFound.visibility = View.VISIBLE
//                                binding.cardDel.visibility = View.GONE
//                                binding.cardCan.visibility = View.GONE
                            }
//                            if (canceled.isEmpty()) {
//                                binding.cardCan.visibility = View.GONE
//                            }
//
//                            if (completed.isEmpty()) {
//                                binding.cardDel.visibility = View.GONE
//                            }
//                            binding.shimmer.visibility = View.GONE
//
//                            binding!!.recyclerViewWomen.adapter =
//                                activity?.let {
//                                    AdapterProduct(
//                                        it, women,
//                                    )
//                                }
//                            binding.shimmerWomen.visibility = View.GONE
//
//                            binding!!.recyclerViewKid.adapter =
//                                activity?.let {
//                                    AdapterProduct(
//                                        it, kids,
//                                    )
//                                }
//                            binding.shimmerKid.visibility = View.GONE
//
//                            binding!!.recyclerViewSpecial.adapter =
//                                activity?.let {
//                                    AdapterProduct(
//                                        it, response.body()!!.data.posts.data,
//                                    )
//                                }
//                            binding.shimmerSpecial.visibility = View.GONE
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
                        apiCallAllOrderCan()
                    } else {
                        myToast(requireActivity(), t.message.toString())
                        AppProgressBar.hideLoaderDialog()

                    }
                    AppProgressBar.hideLoaderDialog()


                }

            })
    }
}