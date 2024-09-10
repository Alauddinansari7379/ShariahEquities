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
import com.amtech.shariahEquities.Helper.AppProgressBar
import com.amtech.shariahEquities.fragments.adapter.AdapterBasket
import com.amtech.shariahEquities.fragments.adapter.AdapterBasketAllList
import com.amtech.shariahEquities.fragments.adapter.StocksAdapter
import com.amtech.shariahEquities.fragments.model.ModelAddWatchList
import com.amtech.shariahEquities.fragments.model.modelGetBasket.ModelGetBasket
import com.amtech.shariahEquities.modelCompany.ModelCompanyList
import com.amtech.shariahEquities.modelCompany.Result
import com.amtech.shariahEquities.retrofit.ApiClient
import com.amtech.shariahEquities.sharedpreferences.SessionManager
import com.example.tlismimoti.Helper.myToast
import com.sellacha.tlismiherbs.databinding.FragmentStocksBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class BasketsFragment : Fragment(),AdapterBasketAllList.AddWatchList {

    private var _binding: FragmentStocksBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapterBasketAllList: AdapterBasketAllList
     private var count = 0
    private var companyList = mutableListOf<Result>()
     private val selectedCompanies = mutableListOf<Result>()
    private lateinit var sessionManager: SessionManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentStocksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sessionManager = SessionManager(requireContext())
        adapterBasketAllList = AdapterBasketAllList (requireContext(),{ _, _ -> updateSaveButtonVisibility() },this)

        binding.rvCompanyList.apply {
             adapter = adapterBasketAllList
         }

        binding.addSelectedButton.setOnClickListener {
            adapterBasketAllList.setShowCheckboxes(true)
            binding.btnSave.visibility = View.VISIBLE
            binding.edtOwnName.visibility = View.VISIBLE
            binding.rvCompanyList.visibility = View.VISIBLE
        }

        binding.btnSave.setOnClickListener {
            if (binding.edOwnName.text!!.isEmpty()) {
                binding.edOwnName.error = "Enter Name"
                binding.edOwnName.requestFocus()
                return@setOnClickListener
            }
            val selectedItems = adapterBasketAllList.getSelectedItems()
            selectedCompanies.clear()
            selectedCompanies.addAll(selectedItems)

            when {
                selectedCompanies.size > 10 -> {
                    myToast(context as Activity, "You can't save more than 10 items")
                }

                selectedCompanies.isEmpty() -> {
                    myToast(context as Activity, "Please select any items")
                }

                else -> {
                    addBasketList()
//                    myToast(context as Activity, "${selectedCompanies.size} items saved")
//                    stocksAdapter.setShowCheckboxes(false)
//                    binding.btnSave.visibility = View.GONE
                }
            }
        }

        binding.edtSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                performSearch(s.toString())
                binding.rvBasketList.visibility=View.GONE
                binding.rvCompanyList.visibility=View.VISIBLE
            }

            override fun afterTextChanged(s: Editable?) {}

        })

        binding.rvCompanyList.visibility = View.GONE
        apiCallGetCompanyList()
        apiCallGetBasketList()
    }

    private fun performSearch(query: String) {
        val trimmedQuery = query.trim()

        if (trimmedQuery.isEmpty()) {
            binding.rvCompanyList.visibility = View.GONE
        } else {
            val filteredList = companyList.filter { result ->
                result.name_of_company.contains(trimmedQuery, ignoreCase = true) ||
                        result.symbol.contains(trimmedQuery, ignoreCase = true)
            }
            if (adapterBasketAllList.currentList != filteredList) {
                adapterBasketAllList.submitList(filteredList)
            }
            binding.rvCompanyList.visibility =
                if (filteredList.isNotEmpty()) View.VISIBLE else View.GONE
        }
    }


    private fun updateSaveButtonVisibility() {
        val hasSelectedItems = adapterBasketAllList.getSelectedItems().isNotEmpty()
        binding.btnSave.visibility = if (hasSelectedItems) View.VISIBLE else View.GONE
    }

    private fun apiCallGetCompanyList() {
       // AppProgressBar.showLoaderDialog(context)
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
                                adapterBasketAllList.submitList(companyList)
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

    fun addBasketList() {
        AppProgressBar.showLoaderDialog(context)
        val companyIdsArray = selectedCompanies.map { it.id.toString() }
        val formattedString = companyIdsArray.joinToString(prefix = "[\"", postfix = "\"]", separator = "\", \"")
//        val formattedString = companyIdsArray.joinToString(prefix = "[", postfix = "]", separator = ",")
//        val companyIdsArray = selectedCompanies.map { it.id.toString() }
//        val formattedString = companyIdsArray.joinToString(prefix = "[", postfix = "]", separator = ",")


        ApiClient.apiService.addBasket(
            sessionManager.id.toString(),
            binding.edOwnName.text!!.toString(),
//            companyIdsArray.joinToString(",")
            formattedString
        )
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

                            response.isSuccessful && response.body()?.status == 1 != null -> {
                                count=0
                                myToast(context as Activity, "${selectedCompanies.size} Stocks Added")
                                adapterBasketAllList.setShowCheckboxes(false)
                                binding.btnSave.visibility = View.GONE
                                binding.edtOwnName.visibility = View.GONE
                                binding.edOwnName.setText("")
                                selectedCompanies.clear()
                                adapterBasketAllList.clearSelectedItems()
                                apiCallGetBasketList()


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
                    if (count <= 3) {
                        addBasketList()
                    } else {
                        myToast(context as Activity, "Something went wrong")
                    }
                    AppProgressBar.hideLoaderDialog()
                }
            })
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
                        addWatchList(compenyId)
                    } else {
                        myToast(context as Activity, "Something went wrong")
                    }
                    AppProgressBar.hideLoaderDialog()
                }
            })
    }
    private fun apiCallGetBasketList() {
        AppProgressBar.showLoaderDialog(context)
        ApiClient.apiService.allBasketList(sessionManager.id.toString())
            .enqueue(object : Callback<ModelGetBasket> {
                @SuppressLint("SetTextI18n")
                override fun onResponse(
                    call: Call<ModelGetBasket>, response: Response<ModelGetBasket>
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
                                binding.rvBasketList.apply {
                                     adapter = AdapterBasket(requireContext(),response.body()!!.result)
                                }
                             }

                            else -> myToast(context as Activity, "Unexpected error")
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        myToast(context as Activity, "Something went wrong")
                    }
                }

                override fun onFailure(call: Call<ModelGetBasket>, t: Throwable) {
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


}
