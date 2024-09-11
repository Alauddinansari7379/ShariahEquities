package com.amtech.shariahEquities.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.amtech.shariahEquities.Helper.AppProgressBar
import com.amtech.shariahEquities.fragments.adapter.AdapterBasket
import com.amtech.shariahEquities.fragments.adapter.AdapterPopupBasket
import com.amtech.shariahEquities.fragments.model.ModelAddWatchList
import com.amtech.shariahEquities.fragments.model.modelGetBasket.ModelGetBasket
import com.amtech.shariahEquities.modelCompany.ModelCompanyList
import com.amtech.shariahEquities.modelCompany.Result
import com.amtech.shariahEquities.retrofit.ApiClient
import com.amtech.shariahEquities.sharedpreferences.SessionManager
import com.example.tlismimoti.Helper.myToast
import com.sellacha.tlismiherbs.R
import com.sellacha.tlismiherbs.databinding.FragmentBasketBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class BasketsFragment : Fragment(){

    private lateinit var dialogAdapter: AdapterPopupBasket
    private var _binding: FragmentBasketBinding? = null
    private val binding get() = _binding!!

     private var count = 0
     private var countL = 0
     private var countAdd = 0
    private var companyList = mutableListOf<Result>()
    private var allbasketList = ArrayList<com.amtech.shariahEquities.fragments.model.modelGetBasket.Result>()
    private val selectedCompanies = mutableListOf<Result>()
    private lateinit var sessionManager: SessionManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBasketBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sessionManager = SessionManager(requireContext())


        binding.addSelectedButton.setOnClickListener {
            showCompanySelectionDialog()
        }

        binding.edtSearch.addTextChangedListener { str ->
            setRecyclerViewAdapter(allbasketList.filter {
                it.basketname!=null && it.basketname.contains(str.toString(), ignoreCase = true)
            } as ArrayList<com.amtech.shariahEquities.fragments.model.modelGetBasket.Result>)
        }

        apiCallGetCompanyList()
        apiCallGetBasketList()
    }

@SuppressLint("SetTextI18n")
private fun setRecyclerViewAdapter(userList: ArrayList<com.amtech.shariahEquities.fragments.model.modelGetBasket.Result>) {
    if (binding.rvBasketList != null) {
        binding.rvBasketList.apply {
            adapter = AdapterBasket(requireContext(), userList)
        }
    }
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
                            response.code() == 500 -> activity?.let { myToast(it, "Server Error") }
                            response.code() == 404 -> activity?.let {
                                myToast(
                                    it,
                                    "Something went wrong"
                                )
                            }

                            response.isSuccessful && response.body() != null -> {
                                count=0
                                companyList = response.body()!!.result.toMutableList()
                                dialogAdapter.submitList(companyList)
                            }

                            else -> activity?.let { myToast(it, "Unexpected error") }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                       // activity?.let { myToast(it, "Something went wrong") }
                    }
                }

                override fun onFailure(call: Call<ModelCompanyList>, t: Throwable) {
                    count++
                    if (count <= 3) {
                        apiCallGetCompanyList()
                    } else {
                        activity?.let { myToast(it, "Something went wrong") }
                    }
                    AppProgressBar.hideLoaderDialog()
                }
            })
    }

    fun addBasketList(basket: String) {
        AppProgressBar.showLoaderDialog(context)
        val companyIdsArray = selectedCompanies.map { it.id.toString() }
        val formattedString =
            companyIdsArray.joinToString(prefix = "[\"", postfix = "\"]", separator = "\", \"")
//        val formattedString = companyIdsArray.joinToString(prefix = "[", postfix = "]", separator = ",")
//        val companyIdsArray = selectedCompanies.map { it.id.toString() }
//        val formattedString = companyIdsArray.joinToString(prefix = "[", postfix = "]", separator = ",")


        ApiClient.apiService.addBasket(
            sessionManager.id.toString(),
            basket,
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
                            response.code() == 500 -> activity?.let { myToast(it, "Server Error") }
                            response.code() == 404 -> activity?.let {
                                myToast(
                                    it,
                                    "Something went wrong"
                                )
                            }

                            response.isSuccessful && response.body()?.status == 1 != null -> {
                                countL = 0
                                myToast(
                                    context as Activity,
                                    "${selectedCompanies.size} Stocks Added"
                                )
                                 binding.btnSave.visibility = View.GONE
                                binding.edtOwnName.visibility = View.GONE
                                binding.edOwnName.setText("")
                                selectedCompanies.clear()
                                 apiCallGetBasketList()


                            }

                            else -> activity?.let { myToast(it, "Unexpected error") }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                       // activity?.let { myToast(it, "Something went wrong") }
                    }
                }

                override fun onFailure(call: Call<ModelAddWatchList>, t: Throwable) {
                    countL++
                    if (countL <= 3) {
                        addBasketList(basket)
                    } else {
                        activity?.let { myToast(it, t.message.toString()) }
                    }
                    AppProgressBar.hideLoaderDialog()
                }
            })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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
                            response.code() == 500 -> activity?.let { myToast(it, "Server Error") }
                            response.code() == 404 -> activity?.let {
                                myToast(
                                    it,
                                    "Something went wrong"
                                )
                            }

                            response.isSuccessful && response.body() != null -> {
                                countAdd=0
                                allbasketList = response.body()!!.result
                                binding.rvBasketList.apply {
                                    adapter = AdapterBasket(requireContext(), allbasketList)
                                }
                            }

                            else -> activity?.let { myToast(it, "Unexpected error") }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                      //  activity?.let { myToast(it, "Something went wrong") }
                    }
                }

                override fun onFailure(call: Call<ModelGetBasket>, t: Throwable) {
                    countAdd++
                    if (countAdd <= 2) {
                        apiCallGetCompanyList()
                    } else {
                        activity?.let { myToast(it, t.message.toString()) }
                    }
                    AppProgressBar.hideLoaderDialog()
                }
            })
    }


    private fun showCompanySelectionDialog() {
        val dialogView =
            LayoutInflater.from(requireContext()).inflate(R.layout.popup_add_basket, null)
        val recyclerView = dialogView.findViewById<RecyclerView>(R.id.rvDialogCompanyList)
        val btnSave = dialogView.findViewById<Button>(R.id.btnDialogSave)
        val etOwnName = dialogView.findViewById<TextView>(R.id.edOwnName)
        val edtSearch = dialogView.findViewById<EditText>(R.id.edtSearch)
        val close = dialogView.findViewById<ImageView>(R.id.imgClose)

        dialogAdapter = AdapterPopupBasket(requireContext(), { result, isChecked -> },)


        recyclerView.adapter = dialogAdapter
        dialogAdapter.submitList(companyList)
        edtSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s!!.isNotEmpty()){
                    recyclerView.scrollToPosition(0)
                }
                val filteredList = companyList.filter {
                    it.name_of_company.contains(s.toString(), ignoreCase = true) ||
                            it.symbol.contains(s.toString(), ignoreCase = true)
                }
                dialogAdapter.submitList(filteredList)
            }

            override fun afterTextChanged(s: Editable?) {}
        })
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        close.setOnClickListener {
            dialog.dismiss()
        }
        btnSave.setOnClickListener {
            val selectedItems = dialogAdapter.getSelectedItems()
            selectedCompanies.clear()
            selectedCompanies.addAll(selectedItems)
            if (etOwnName.text!!.isEmpty()) {
                etOwnName.error = "Enter Name"
                etOwnName.requestFocus()
                return@setOnClickListener
            }
            if (selectedCompanies.size > 10) {
                activity?.let { it1 -> myToast(it1, "You can't save more than 10 items") }
            } else if (selectedCompanies.isEmpty()) {
                activity?.let { it1 -> myToast(it1, "Please select any items") }
            } else {
                addBasketList(etOwnName.text!!.toString())
                
                dialog.dismiss()

            }
        }

        dialog.show()
    }

}
