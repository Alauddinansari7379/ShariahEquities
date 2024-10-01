package com.amtech.shariahEquities.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import com.amtech.shariahEquities.Helper.AppProgressBar
import com.amtech.shariahEquities.fragments.adapter.StocksAdapter
import com.amtech.shariahEquities.modelCompany.ModelCompanyList
import com.amtech.shariahEquities.modelCompany.Result
import com.amtech.shariahEquities.retrofit.ApiClient
import com.amtech.shariahEquities.sharedpreferences.SessionManager
import com.example.tlismimoti.Helper.isInternetAvailable
import com.example.tlismimoti.Helper.myToast
import com.sellacha.tlismiherbs.R
import com.sellacha.tlismiherbs.databinding.FragmentStocksBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StocksFragment : Fragment() {
    private var _binding: FragmentStocksBinding? = null
    private val binding get() = _binding!!

    private var count = 0
    private var companyList = ArrayList<Result>()
    lateinit var sessionManager: SessionManager
    private var selectedCompliance: String = "All"
    private var selectedExchange: String = "All"
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
        setupSpinner()
        setupSpinner2()
//        if (sessionManager.subscribed.toString()=="0"){
//            binding.edtSearch.setEnabled(false)
//            binding.edtSearch.isFocusable = false
//            binding.edtSearch.setCursorVisible(false)
//            binding.imgLock.visibility=View.VISIBLE
//        }
        binding.edtSearch.addTextChangedListener { str ->
            setRecyclerViewAdapter(companyList.filter {
                it.name_of_company != null && it.name_of_company.contains(
                    str.toString(),
                    ignoreCase = true
                ) || it.nse_symbol_bse_script_id != null && it.nse_symbol_bse_script_id.contains(
                    str.toString(),
                    ignoreCase = true
                )
            } as ArrayList<Result>)
        }

        apiCallGetCompanyList()
    }

    @SuppressLint("SetTextI18n")
    private fun setRecyclerViewAdapter(userList: ArrayList<Result>) {
        if (binding.rvCompanyList != null) {
            binding.rvCompanyList.apply {
                adapter = StocksAdapter(requireContext(), userList)
//
            }
        }
    }

//    private fun setupSpinner() {
//        val filterOptions = ArrayList<String>()
//        filterOptions.add("All")
//        filterOptions.add("Compliant")
//        filterOptions.add("Non-Compliant")
//
//        val adapter = ArrayAdapter(requireContext(), R.layout.simple_list_item_1, filterOptions)
//        adapter.setDropDownViewResource(R.layout.simple_list_item_1)
//        binding.spinnerFilter.adapter = adapter
//        binding.spinnerFilter.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//            override fun onItemSelected(
//                parent: AdapterView<*>,
//                view: View?,
//                position: Int,
//                id: Long
//            ) {
//                val selectedFilter = filterOptions[position]
//                filterListByCompliance(selectedFilter)
//            }
//
//            override fun onNothingSelected(parent: AdapterView<*>) {
//
//            }
//        }
//        binding.spinnerFilter.setSelection(0)
//    }
//    private fun setupSpinner2() {
//        val filterOptions = ArrayList<String>()
//        filterOptions.add("All")
//        filterOptions.add("BSE")
//        filterOptions.add("NSE")
//
//        val adapter = ArrayAdapter(requireContext(), R.layout.simple_list_item_1, filterOptions)
//        adapter.setDropDownViewResource(R.layout.simple_list_item_1)
//        binding.spinnerFilter2.adapter = adapter
//        binding.spinnerFilter2.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//            override fun onItemSelected(
//                parent: AdapterView<*>,
//                view: View?,
//                position: Int,
//                id: Long
//            ) {
//                val selectedFilter = filterOptions[position]
//                filterListByCompliance2(selectedFilter)
//            }
//
//            override fun onNothingSelected(parent: AdapterView<*>) {
//
//            }
//        }
//        binding.spinnerFilter2.setSelection(0)
//    }
//
//    private fun filterListByCompliance(selectedFilter: String) {
//        val filteredList = when (selectedFilter) {
//            "All" -> companyList
//            "Compliant" -> companyList.filter { it.final == "PASS" }
//            "Non-Compliant" -> companyList.filter { it.final == "FAIL" }
//            else -> companyList
//        }
//        setRecyclerViewAdapter(ArrayList(filteredList))
//    }
//    private fun filterListByCompliance2(selectedFilter: String) {
//        val filteredList = when (selectedFilter) {
//            "All" -> companyList
//            "BSE" -> companyList.filter { it.exchange == "BSE" }
//            "NSE" -> companyList.filter { it.exchange == "NSE" }
//            else -> companyList
//        }
//        setRecyclerViewAdapter(ArrayList(filteredList))
//    }

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
                            response.code() == 500 -> activity?.let { myToast(it, "Server Error") }
                            response.code() == 404 -> activity?.let {
                                myToast(
                                    it,
                                    "Something went wrong"
                                )
                            }

                            response.isSuccessful && response.body() != null -> {
                                companyList = response.body()!!.result
//                                if (sessionManager.subscribed.toString()=="0"){
//                                    val firstItem = listOf(companyList[0],companyList[1],companyList[2])
//                                    binding.rvCompanyList.apply {
//                                        adapter = StocksAdapter(requireContext(),firstItem)
//
//                                    }
//                                }else{
//                                filterListByCompliance("All")
                                binding.rvCompanyList.apply {
                                    adapter = StocksAdapter(requireContext(), companyList)
//
                                    // }
                                }


                            }

                            else -> activity?.let { myToast(it, "Unexpected error") }

                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        activity?.let { myToast(it, "Something went wrong") }
                    }
                }

                override fun onFailure(call: Call<ModelCompanyList>, t: Throwable) {
                    count++
                    if (count <= 2) {
                        apiCallGetCompanyList()
                    } else {
                        activity?.let { myToast(it, "Something went wrong") }
                    }
                    AppProgressBar.hideLoaderDialog()
                }
            })
    }



    private fun setupSpinner() {
        val filterOptions = arrayListOf("All", "Compliant", "Non-Compliant")

        val adapter = ArrayAdapter(requireContext(), R.layout.simple_list_item_1, filterOptions)
        adapter.setDropDownViewResource(R.layout.simple_list_item_1)
        binding.spinnerFilter.adapter = adapter
        binding.spinnerFilter.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                selectedCompliance = filterOptions[position]
                filterList()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
        binding.spinnerFilter.setSelection(0)
    }

    private fun setupSpinner2() {
        val filterOptions = arrayListOf("All", "BSE", "NSE")

        val adapter = ArrayAdapter(requireContext(), R.layout.simple_list_item_1, filterOptions)
        adapter.setDropDownViewResource(R.layout.simple_list_item_1)
        binding.spinnerFilter2.adapter = adapter
        binding.spinnerFilter2.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                selectedExchange = filterOptions[position]
                filterList()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
        binding.spinnerFilter2.setSelection(0)
    }

    private fun filterList() {
        var filteredList = companyList

        filteredList = when (selectedCompliance) {
            "Compliant" -> filteredList.filter { it.final == "PASS" } as ArrayList<Result>
            "Non-Compliant" -> filteredList.filter { it.final == "FAIL" } as ArrayList<Result>
            else -> filteredList
        }


        filteredList = when (selectedExchange) {
            "BSE" -> filteredList.filter { it.exchange == "BSE" } as ArrayList<Result>
            "NSE" -> filteredList.filter { it.exchange == "NSE" } as ArrayList<Result>
            else -> filteredList
        }

        setRecyclerViewAdapter(ArrayList(filteredList))
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}
