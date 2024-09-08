package com.amtech.shariahEquities.notification

import android.app.Activity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import com.amtech.shariahEquities.sharedpreferences.SessionManager
import com.example.tlismimoti.Helper.myToast
import com.sellacha.tlismiherbs.databinding.FragmentStocksBinding
import com.sellacha.tlismiherbs.databinding.FragmentWatchlistBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WatchlistFragment : Fragment() {
    private var _binding: FragmentWatchlistBinding? = null
    private val binding get() = _binding!!
    private var watchList = ArrayList<Result>()
    private var count = 0
    lateinit var sessionManager: SessionManager
    private lateinit var watchListAdapter: WatchListAdapter
    private var companyList = mutableListOf<Result>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentWatchlistBinding.inflate(inflater, container, false)
        sessionManager=SessionManager(requireContext())
        apiCallGetWatchList()
        initRecyclerView()
        binding.edtSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                performSearch(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        return binding.root
    }

    private fun initRecyclerView() {
        watchListAdapter = WatchListAdapter(requireContext(), watchList)
        binding.rvWatchlist.apply {
            adapter = watchListAdapter
         }
    }
    private fun apiCallGetWatchList() {
        AppProgressBar.showLoaderDialog(context)
        ApiClient.apiService.getWatchList(sessionManager.id.toString())
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
                          //  myToast(context as Activity, responseBody.message)
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
                        apiCallGetWatchList()
                    } else {
                        myToast(context as Activity, "Something went wrong")
                    }
                    AppProgressBar.hideLoaderDialog()
                }
            })
    }

    private fun performSearch(query: String) {
        val trimmedQuery = query.trim()

        // Show all data when query is empty
        val filteredList = if (trimmedQuery.isEmpty()) {
            watchList // Show the full list if no search query is entered
        } else {
            watchList.filter { result ->
                result.name_of_company?.contains(trimmedQuery, ignoreCase = true) == true ||
                        result.symbol?.contains(trimmedQuery, ignoreCase = true) == true
            }
        }

        // Update the adapter with the filtered list
        setRecyclerViewAdapter(ArrayList(filteredList))
    }

    private fun setRecyclerViewAdapter(filteredList: ArrayList<Result>) {
        watchListAdapter = WatchListAdapter(requireContext(), filteredList) // Update the adapter's data
        binding.rvWatchlist.adapter = watchListAdapter // Set the new adapter
        watchListAdapter.notifyDataSetChanged() // Notify the adapter about the data change
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
