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
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.amtech.shariahEquities.Helper.AppProgressBar
import com.amtech.shariahEquities.fragments.adapter.AdapterBasket
import com.amtech.shariahEquities.fragments.adapter.StocksAdapter
import com.amtech.shariahEquities.fragments.model.ModelAddWatchList
import com.amtech.shariahEquities.modelCompany.ModelCompanyList
import com.amtech.shariahEquities.modelCompany.Result
import com.amtech.shariahEquities.retrofit.ApiClient
import com.amtech.shariahEquities.sharedpreferences.SessionManager
import com.example.tlismimoti.Helper.myToast
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

        binding.edtSearch.addTextChangedListener { str ->
            setRecyclerViewAdapter(companyList.filter {
                it.name_of_company!=null && it.name_of_company.contains(str.toString(), ignoreCase = true)||it.symbol!=null && it.symbol.contains(str.toString(), ignoreCase = true)
            } as ArrayList<Result>)
        }

        apiCallGetCompanyList()
    }

    @SuppressLint("SetTextI18n")
    private fun setRecyclerViewAdapter(userList: ArrayList<Result>) {
              if (binding.rvCompanyList != null) {
                   binding.rvCompanyList.apply {
                      adapter = StocksAdapter(requireContext(),userList)
//
                  }
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
                            response.code() == 500 -> activity?.let { myToast(it, "Server Error") }
                            response.code() == 404 -> activity?.let {
                                myToast(
                                    it,
                                    "Something went wrong"
                                )
                            }

                            response.isSuccessful && response.body() != null -> {
//
                                companyList = response.body()!!.result
                                binding.rvCompanyList.apply {
                                    adapter = StocksAdapter(requireContext(),response.body()!!.result)
//
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}
