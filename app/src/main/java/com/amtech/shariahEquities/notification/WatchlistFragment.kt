package com.amtech.shariahEquities.notification

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.amtech.shariahEquities.Helper.AppProgressBar
import com.amtech.shariahEquities.notification.adapter.WatchListAdapter
import com.amtech.shariahEquities.notification.modelwatchlist.ModelWatchList
import com.amtech.shariahEquities.notification.modelwatchlist.Result
import com.amtech.shariahEquities.retrofit.ApiClient
import com.example.tlismimoti.Helper.myToast
import com.sellacha.tlismiherbs.databinding.FragmentStocksBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WatchlistFragment : Fragment() {
    private var _binding: FragmentStocksBinding? = null
    private val binding get() = _binding!!
    private var watchList = ArrayList<Result>()
    private var count = 0
    private lateinit var watchListAdapter: WatchListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentStocksBinding.inflate(inflater, container, false)

        initRecyclerView()
        apiCallgetWatchList("1")

        return binding.root
    }

    private fun initRecyclerView() {
        watchListAdapter = WatchListAdapter(requireContext(), watchList)
        binding.rvCompanyList.apply {
            adapter = watchListAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }
    private fun apiCallgetWatchList(id: String) {
        AppProgressBar.showLoaderDialog(context)
        ApiClient.apiService.getWatchList(id)
            .enqueue(object : Callback<ModelWatchList> {
                override fun onResponse(
                    call: Call<ModelWatchList>, response: Response<ModelWatchList>
                ) {
                    AppProgressBar.hideLoaderDialog()
                    try {
                        if (response.isSuccessful && response.body() != null) {
                            val responseBody = response.body()!!
                            watchList.clear()
                            watchList.addAll(responseBody.result)
                            watchListAdapter.notifyDataSetChanged()
                            myToast(context as Activity, responseBody.message)
                        } else {
                            myToast(context as Activity, "Unexpected error")
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        myToast(context as Activity, "Something went wrong")
                    }
                }

                override fun onFailure(call: Call<ModelWatchList>, t: Throwable) {
                    count++
                    if (count <= 2) {
                        apiCallgetWatchList(id)
                    } else {
                        myToast(context as Activity, "Something went wrong")
                    }
                    AppProgressBar.hideLoaderDialog()
                }
            })
    }



//    private fun apiCallgetWatchList(id: String) {
//        AppProgressBar.showLoaderDialog(context)
//        ApiClient.apiService.getWatchList(id)
//            .enqueue(object : Callback<ModelWatchList> {
//                @SuppressLint("SetTextI18n")
//                override fun onResponse(
//                    call: Call<ModelWatchList>, response: Response<ModelWatchList>
//                ) {
//                    AppProgressBar.hideLoaderDialog()
//                    try {
//                        when {
//                            response.code() == 500 -> myToast(context as Activity, "Server Error")
//                            response.code() == 404 -> myToast(
//                                context as Activity,
//                                "Something went wrong"
//                            )
//                            response.isSuccessful && response.body() != null -> {
//                                watchList.clear() // Clear the current list
//                                watchList.addAll(response.body()!!.result) // Add new data
//                                watchListAdapter.notifyDataSetChanged() // Refresh RecyclerView
//                                myToast(context as Activity, response.body()!!.message)
//                            }
//                            else -> myToast(context as Activity, "Unexpected error")
//                        }
//                    } catch (e: Exception) {
//                        e.printStackTrace()
//                        myToast(context as Activity, "Something went wrong")
//                    }
//                }
//
//                override fun onFailure(call: Call<ModelWatchList>, t: Throwable) {
//                    count++
//                    if (count <= 2) {
//                        // Retry API call up to 2 times
//                        apiCallgetWatchList(id)
//                    } else {
//                        myToast(context as Activity, "Something went wrong")
//                    }
//                    AppProgressBar.hideLoaderDialog()
//                }
//            })
//    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
