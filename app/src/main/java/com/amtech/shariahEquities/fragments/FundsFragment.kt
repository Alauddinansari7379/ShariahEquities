package com.amtech.shariahEquities.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.amtech.shariahEquities.Helper.AppProgressBar
import com.amtech.shariahEquities.fragments.adapter.FundsAdapter
import com.amtech.shariahEquities.fragments.adapter.StocksAdapter
import com.amtech.shariahEquities.fragments.model.ModelAddWatchList
import com.amtech.shariahEquities.modelCompany.ModelCompanyList
import com.amtech.shariahEquities.modelCompany.Result
import com.amtech.shariahEquities.retrofit.ApiClient
import com.amtech.shariahEquities.sharedpreferences.SessionManager
import com.example.tlismimoti.Helper.myToast
import com.sellacha.tlismiherbs.R
import com.sellacha.tlismiherbs.databinding.FragmentStocksBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FundsFragment : Fragment(),FundsAdapter.AddWatchList {
    private var _binding: FragmentStocksBinding? = null
    private val binding get() = _binding!!

    private lateinit var fundsAdapter: FundsAdapter
    private var count = 0
    private lateinit var sessionManager: SessionManager
    private var companyList = mutableListOf<Result>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentStocksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.llCreateWon.visibility = View.GONE
        sessionManager = SessionManager(requireContext())
        fundsAdapter = FundsAdapter (requireContext(),{ result, isChecked -> },this)
        binding.rvCompanyList.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = fundsAdapter
        }
        binding.edtSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                performSearch(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}

        })

        apiCallGetCompanyList()
    }


    private fun performSearch(query: String) {
        val trimmedQuery = query.trim()

        if (trimmedQuery.isEmpty()) {
            if (fundsAdapter.currentList != companyList) {
                fundsAdapter.submitList(companyList)
            }
            binding.rvCompanyList.visibility = View.VISIBLE
        } else {
            val filteredList = companyList.filter { result ->
                result.name_of_company.contains(trimmedQuery, ignoreCase = true) ||
                        result.symbol.contains(trimmedQuery, ignoreCase = true)
            }
            if (fundsAdapter.currentList != filteredList) {
                fundsAdapter.submitList(filteredList)
            }
            binding.rvCompanyList.visibility =
                if (filteredList.isNotEmpty()) View.VISIBLE else View.GONE
        }
    }


    private fun apiCallGetCompanyList() {
        AppProgressBar.showLoaderDialog(context)
        ApiClient.apiService.getCompanyList()
            .enqueue(object : Callback<ModelCompanyList> {
                @SuppressLint("SetTextI18n")
                override fun onResponse(
                    call: Call<ModelCompanyList>, response: Response<ModelCompanyList>
                ) {
                    AppProgressBar.hideLoaderDialog()
                    try {
                        when {
                            response.code() == 500 -> myToast(context as Activity, "Server Error")
                            response.code() == 404 -> myToast(
                                context as Activity,
                                "Something went wrong"
                            )

                            response.isSuccessful && response.body() != null -> {
                                companyList =
                                    response.body()!!.result.toMutableList()
                                fundsAdapter.submitList(companyList)
                            }

                            else -> myToast(context as Activity, "Unexpected error")
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        myToast(context as Activity, "Something went wrong")
                    }
                }

                override fun onFailure(call: Call<ModelCompanyList>, t: Throwable) {
                    count++
                    if (count <= 2) {
                        apiCallGetCompanyList()
                    } else {
                        myToast(context as Activity, "Something went wrong")
                    }
                    AppProgressBar.hideLoaderDialog()
                }
            })
    }

    private fun apiCallAddWatchList() {

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun addWatchList(compenyId: String) {
        AppProgressBar.showLoaderDialog(context)
        ApiClient.apiService.createWatchlist(sessionManager.id.toString(),compenyId)
            .enqueue(object : Callback<ModelAddWatchList> {
                @SuppressLint("SetTextI18n")
                override fun onResponse(
                    call: Call<ModelAddWatchList>, response: Response<ModelAddWatchList>
                ) {
                    AppProgressBar.hideLoaderDialog()
                    try {
                        when {
                            response.code() == 500 -> myToast(context as Activity, "Server Error")
                            response.code() == 404 -> myToast(
                                context as Activity,
                                "Something went wrong"
                            )

                            response.isSuccessful && response.body()?.status==1 != null -> {
                                myToast(context as Activity, "Added in Watchlist")

                            }

                            else -> myToast(context as Activity, "Unexpected error")
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        myToast(context as Activity, "Something went wrong")
                    }
                }

                override fun onFailure(call: Call<ModelAddWatchList>, t: Throwable) {
                    count++
                    if (count <= 2) {
                        apiCallAddWatchList()
                    } else {
                        myToast(context as Activity, "Something went wrong")
                    }
                    AppProgressBar.hideLoaderDialog()
                }
            })    }
}
