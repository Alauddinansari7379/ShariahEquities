package com.amtech.shariahEquities.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.provider.ContactsContract
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
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
class StocksFragment : Fragment() {
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
        binding.llCreateWon.visibility = View.GONE

        stocksAdapter = StocksAdapter { _, _ -> updateSaveButtonVisibility() }
        binding.rvCompanyList.apply {
             adapter = stocksAdapter
        }

        binding.addSelectedButton.setOnClickListener {
            stocksAdapter.setShowCheckboxes(true)
            binding.btnSave.visibility = View.VISIBLE
        }

        binding.btnSave.setOnClickListener {
            selectedCompanies.clear()
            selectedCompanies.addAll(stocksAdapter.getSelectedItems())
            myToast(context as Activity, "${selectedCompanies.size} items saved")
            stocksAdapter.setShowCheckboxes(false)
            binding.btnSave.visibility = View.GONE
        }

        // Update search functionality
        binding.edtSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                performSearch(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        apiCallgetCompanyList()
    }

    private fun performSearch(query: String) {
        val trimmedQuery = query.trim()

        // Show all data when query is empty
        val filteredList = if (trimmedQuery.isEmpty()) {
            companyList
        } else {
            companyList.filter { result ->
                result.name_of_company?.contains(trimmedQuery, ignoreCase = true) == true ||
                        result.symbol?.contains(trimmedQuery, ignoreCase = true) == true
            }
        }

        setRecyclerViewAdapter(filteredList)
    }

    @SuppressLint("SetTextI18n")
    private fun setRecyclerViewAdapter(userList: List<Result>) {
        if (binding.rvCompanyList != null) {
            stocksAdapter.submitList(userList)
        }
    }

    private fun updateSaveButtonVisibility() {
        val hasSelectedItems = stocksAdapter.getSelectedItems().isNotEmpty()
        binding.btnSave.visibility = if (hasSelectedItems) View.VISIBLE else View.GONE
    }

    private fun apiCallgetCompanyList() {
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
//                                companyList =
//                                    response.body()!!.result.toMutableList()


                                companyList = response.body()!!.result
                                    .sortedBy { it.name_of_company?.toLowerCase() }
                                    .toMutableList()
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
                        apiCallgetCompanyList()
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
