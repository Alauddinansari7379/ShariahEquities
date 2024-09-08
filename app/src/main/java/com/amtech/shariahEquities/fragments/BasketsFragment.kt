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
import com.amtech.shariahEquities.fragments.adapter.StocksAdapter
import com.amtech.shariahEquities.modelCompany.ModelCompanyList
import com.amtech.shariahEquities.modelCompany.Result
import com.amtech.shariahEquities.retrofit.ApiClient
import com.example.tlismimoti.Helper.myToast
import com.sellacha.tlismiherbs.databinding.FragmentStocksBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class BasketsFragment : Fragment() {

    private var _binding: FragmentStocksBinding? = null
    private val binding get() = _binding!!

    private lateinit var stocksAdapter: StocksAdapter
    private var count = 0
    private var companyList = mutableListOf<Result>()
    private val selectedCompanies = mutableListOf<Result>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentStocksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        stocksAdapter = StocksAdapter { _, _ -> updateSaveButtonVisibility() }
        binding.rvCompanyList.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = stocksAdapter
        }

        binding.addSelectedButton.setOnClickListener {
            stocksAdapter.setShowCheckboxes(true)
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
            val selectedItems = stocksAdapter.getSelectedItems()
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
                    myToast(context as Activity, "${selectedCompanies.size} items saved")
                    stocksAdapter.setShowCheckboxes(false)
                    binding.btnSave.visibility = View.GONE
                }
            }
        }

        binding.edtSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                performSearch(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}

        })

        binding.rvCompanyList.visibility = View.GONE
        apiCallGetCompanyList()
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
            if (stocksAdapter.currentList != filteredList) {
                stocksAdapter.submitList(filteredList)
            }
            binding.rvCompanyList.visibility = if (filteredList.isNotEmpty()) View.VISIBLE else View.GONE
        }
    }


    private fun updateSaveButtonVisibility() {
        val hasSelectedItems = stocksAdapter.getSelectedItems().isNotEmpty()
        binding.btnSave.visibility = if (hasSelectedItems) View.VISIBLE else View.GONE
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
                                stocksAdapter.submitList(companyList)
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
