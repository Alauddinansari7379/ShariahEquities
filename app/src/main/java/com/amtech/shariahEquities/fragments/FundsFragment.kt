package com.amtech.shariahEquities.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import com.amtech.shariahEquities.Helper.AppProgressBar
import com.amtech.shariahEquities.fragments.adapter.FundsAdapter
import com.amtech.shariahEquities.modelCompany.ModelCompanyList
import com.amtech.shariahEquities.modelCompany.Result
import com.amtech.shariahEquities.retrofit.ApiClient
import com.amtech.shariahEquities.sharedpreferences.SessionManager
import com.example.tlismimoti.Helper.myToast
import com.sellacha.tlismiherbs.databinding.FragmentFundsBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FundsFragment : Fragment() {
    private var _binding: FragmentFundsBinding? = null
    private val binding get() = _binding!!
     private var count = 0
    private lateinit var sessionManager: SessionManager
    private var companyList = ArrayList<Result>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFundsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
         sessionManager = SessionManager(requireContext())


        binding.edtSearch.addTextChangedListener { str ->
            setRecyclerViewAdapter(companyList.filter {
                it.name_of_company!=null && it.name_of_company.contains(str.toString(), ignoreCase = true)||it.symbol!=null && it.symbol.contains(str.toString(), ignoreCase = true)
            } as ArrayList<Result>)
        }
//
//        binding.edtSearch.addTextChangedListener(object : TextWatcher {
//            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
//
//            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//                performSearch(s.toString())
//            }
//
//            override fun afterTextChanged(s: Editable?) {}
//
//        })

        apiCallGetCompanyList()
    }

    @SuppressLint("SetTextI18n")
    private fun setRecyclerViewAdapter(userList: ArrayList<Result>) {
        if (binding.rvCompanyList != null) {
            binding.rvCompanyList.apply {
                adapter = FundsAdapter(requireContext(), userList)
            }
//            if (userList.isEmpty()){
//                val firstItem = listOf(companyList[0])
//                binding.rvCompanyList.apply {
//                    adapter = FundsAdapter(requireContext(),firstItem)
//                }
//            }
        }
    }
//    private fun performSearch(query: String) {
//        val trimmedQuery = query.trim()
//
//        if (trimmedQuery.isEmpty()) {
//            if (fundsAdapter.currentList != companyList) {
//                fundsAdapter.submitList(companyList)
//                if (trimmedQuery.isEmpty()) {
//                    binding.rvCompanyList.scrollToPosition(0)  // Reset scroll position
//                }
//            }
//            binding.rvCompanyList.visibility = View.VISIBLE
//        } else {
//            val filteredList = companyList.filter { result ->
//                result.name_of_company.contains(trimmedQuery, ignoreCase = true) ||
//                        result.symbol.contains(trimmedQuery, ignoreCase = true)
//            }
//            if (fundsAdapter.currentList != filteredList) {
//
//                fundsAdapter.submitList(filteredList)
//            }
//            binding.rvCompanyList.visibility =
//                if (filteredList.isNotEmpty()) View.VISIBLE else View.GONE
//        }
//    }


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
                                companyList = response.body()!!.result

                                count=0
                                val firstItem = listOf(companyList[0])

                                // Set up the RecyclerView with the single item
                                binding.rvCompanyList.apply {
                                    adapter = FundsAdapter(requireContext(),firstItem)
                                }

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
