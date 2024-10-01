package com.amtech.shariahEquities.Search

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.amtech.shariahEquities.Helper.AppProgressBar
import com.amtech.shariahEquities.Search.adapter.AdapterSearch
import com.amtech.shariahEquities.fragments.model.ModelAddWatchList
import com.amtech.shariahEquities.modelCompany.ModelCompanyList
import com.amtech.shariahEquities.modelCompany.Result
import com.amtech.shariahEquities.notification.modelwatchlist.ModelWatchList
import com.amtech.shariahEquities.retrofit.ApiClient
import com.amtech.shariahEquities.sharedpreferences.SessionManager
import com.example.tlismimoti.Helper.isInternetAvailable
import com.example.tlismimoti.Helper.myToast
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.sellacha.tlismiherbs.R
import com.sellacha.tlismiherbs.databinding.FragmentCartBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class SearchFragment : Fragment(), AdapterSearch.AddWatchList {
    private lateinit var binding: FragmentCartBinding
    private lateinit var sessionManager: SessionManager
    var shimmerFrameLayout: ShimmerFrameLayout? = null
    val finalTotal = ArrayList<Int>()

    private lateinit var adapterSearch: AdapterSearch
    private var count = 0
    private var companyList = mutableListOf<Result>()
    private val selectedCompanies = mutableListOf<Result>()
    var countRe = 0
    private var watchList =
        ArrayList<com.amtech.shariahEquities.notification.modelwatchlist.Result>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_cart, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentCartBinding.bind(view)
        //  shimmerFrameLayout = view.findViewById(R.id.shimmer)
//        shimmerFrameLayout!!.startShimmer()
        sessionManager = SessionManager(requireContext())
        adapterSearch =
            AdapterSearch(requireContext(), { _, _ -> updateSaveButtonVisibility() }, this)

        val bottomSheetDialog = BottomSheetDialog(requireContext())
        val parentView: View = layoutInflater.inflate(R.layout.login_dialog, null)
        bottomSheetDialog.setContentView(parentView)

        binding.rvCompanyList.apply {
            adapter = adapterSearch
        }


        binding.edtSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.rvCompanyList.visibility = View.VISIBLE
                performSearch(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}

        })

        binding.rvCompanyList.visibility = View.GONE
        apiCallGetCompanyList()
        apiCallGetWatchListRe()
    }

    private fun updateSaveButtonVisibility() {
        val hasSelectedItems = adapterSearch.getSelectedItems().isNotEmpty()
    }

    private fun performSearch(query: String) {
        val trimmedQuery = query.trim()

        if (trimmedQuery.isEmpty()) {
            binding.rvCompanyList.visibility = View.GONE
        } else {
            val filteredList = companyList.filter { result ->
                result.nse_symbol_bse_script_id.contains(trimmedQuery, ignoreCase = true) ||
                        result.name_of_company.contains(trimmedQuery, ignoreCase = true)
            }
            if (adapterSearch.currentList != filteredList) {
                adapterSearch.submitList(filteredList)
            }
            binding.rvCompanyList.visibility =
                if (filteredList.isNotEmpty()) View.VISIBLE else View.GONE
        }
    }

    private fun apiCallGetCompanyList() {
        if (!isInternetAvailable(context as Activity)) {
            myToast(
                context as Activity,
                "No internet connection. Please check your network settings."
            )
            return
        }
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
                                count = 0
                                companyList =
                                    response.body()!!.result.toMutableList()
                                adapterSearch.submitList(companyList)
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
                    if (count <= 3) {
                        apiCallGetCompanyList()
                    } else {
                        myToast(context as Activity, "Something went wrong")
                    }
                    AppProgressBar.hideLoaderDialog()
                }
            })
    }

    private fun apiCallGetWatchListRe() {
        //  AppProgressBar.showLoaderDialog(context)
        if (!isInternetAvailable(context as Activity)) {
            myToast(
                context as Activity,
                "No internet connection. Please check your network settings."
            )
            return
        }
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
                    countRe++
                    if (countRe <= 3) {
                        apiCallGetWatchListRe()
                    } else {
                        myToast(context as Activity, "Something went wrong")
                    }
                    AppProgressBar.hideLoaderDialog()
                }
            })
    }

    override fun addWatchList(compenyId: String) {
        if (!isInternetAvailable(context as Activity)) {
            myToast(
                context as Activity,
                "No internet connection. Please check your network settings."
            )
            return
        }
        AppProgressBar.showLoaderDialog(context)
        if (watchList.any { it.company_id == compenyId }) {
            myToast(context as Activity, "Company is already added in the watchlist.")
            AppProgressBar.hideLoaderDialog()
        } else if ((sessionManager.startDate == "" || sessionManager.endDate == "") && watchList.size >= 25) {
            myToast(
                context as Activity,
                "You can't add more then 25 companies to add more upgrade your profile."
            )
            AppProgressBar.hideLoaderDialog()
        } else {
            ApiClient.apiService.createWatchlist(sessionManager.id.toString(), compenyId)
                .enqueue(object : Callback<ModelAddWatchList> {
                    @SuppressLint("SetTextI18n")
                    override fun onResponse(
                        call: Call<ModelAddWatchList>, response: Response<ModelAddWatchList>
                    ) {
                        AppProgressBar.hideLoaderDialog()
                        try {
                            when {
                                response.code() == 500 -> myToast(
                                    context as Activity,
                                    "Server Error"
                                )

                                response.code() == 404 -> myToast(
                                    context as Activity,
                                    "Something went wrong"
                                )

                                response.isSuccessful && response.body()?.status == 1 != null -> {
                                    myToast(context as Activity, "Added in Watchlist")

                                    apiCallGetWatchListRe()
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
                            addWatchList(compenyId)
                        } else {
                            myToast(context as Activity, "Something went wrong")
                        }
                        AppProgressBar.hideLoaderDialog()
                    }
                })
        }
    }
}