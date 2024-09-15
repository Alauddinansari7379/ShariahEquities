package com.amtech.shariahEquities.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import cn.pedant.SweetAlert.SweetAlertDialog
import com.amtech.shariahEquities.Helper.AppProgressBar
import com.amtech.shariahEquities.fragments.adapter.AdapterBasketList
import com.amtech.shariahEquities.fragments.adapter.FundsAdapter
import com.amtech.shariahEquities.fragments.model.modelBasketList.Company
import com.amtech.shariahEquities.fragments.model.modelBasketList.ModelBasketListId
import com.amtech.shariahEquities.notification.adapter.moduledeletewatchlist.ModuleDeleteWatchList
import com.amtech.shariahEquities.retrofit.ApiClient
import com.amtech.shariahEquities.sharedpreferences.SessionManager
import com.example.tlismimoti.Helper.myToast
import com.sellacha.tlismiherbs.databinding.ActivityBasketListBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BasketsList : AppCompatActivity(),AdapterBasketList.Delete {
    private val context = this@BasketsList
    private lateinit var binding: ActivityBasketListBinding
    lateinit var sessionManager: SessionManager
    var count = 0
    var countDe = 0
    private var basketId = ""
    private var list = ArrayList<Company>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBasketListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sessionManager = SessionManager(context)

        basketId = intent.getStringExtra("basketId").toString()
        apiCallGetBasketById()
        with(binding) {

            imgBack.setOnClickListener {
                onBackPressed()
            }
        }
    }

    private fun apiCallGetBasketById() {
        AppProgressBar.showLoaderDialog(context)
        ApiClient.apiService.getBasketById(sessionManager.id.toString(), basketId)
            .enqueue(object : Callback<ModelBasketListId> {
                @SuppressLint("SetTextI18n")
                override fun onResponse(
                    call: Call<ModelBasketListId>, response: Response<ModelBasketListId>
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
                                list = response.body()!!.companies
                                count = 0
                                //  val firstItem = listOf(companyList[0])

                                // Set up the RecyclerView with the single item
                                binding.rvCompanyList.apply {
                                    adapter = AdapterBasketList(this@BasketsList, list,this@BasketsList)
                                }

                            }

                            else -> myToast(context as Activity, "Unexpected error")
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        myToast(context as Activity, "Something went wrong")
                    }
                }

                override fun onFailure(call: Call<ModelBasketListId>, t: Throwable) {
                    count++
                    if (count <= 3) {
                        apiCallGetBasketById()
                    } else {
                        myToast(context as Activity, "Something went wrong")
                    }
                    AppProgressBar.hideLoaderDialog()
                }
            })
    }



    private fun showDeleteConfirmationDialog(id:String) {
        val sweetAlertDialog = SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
            .setTitleText("Are you sure you want to delete this item?")
            .setConfirmText("Yes")
            .setCancelText("No")
            .showCancelButton(true)
            .setConfirmClickListener { sDialog ->
                sDialog.dismissWithAnimation()
                apiDeleteBasket(id)

            }
            .setCancelClickListener { sDialog ->
                sDialog.dismissWithAnimation()
            }

        sweetAlertDialog.setCanceledOnTouchOutside(false)
        sweetAlertDialog.setCancelable(false)
        sweetAlertDialog.show()
    }

    override fun delete(id:String) {
        showDeleteConfirmationDialog(id)
    }
    private fun apiDeleteBasket(id:String){
        ApiClient.apiService.deleteBasketSingle(sessionManager.id.toString(),basketId,id)
            .enqueue(object : Callback<ModuleDeleteWatchList> {
                override fun onResponse(
                    call: Call<ModuleDeleteWatchList>,
                    response: Response<ModuleDeleteWatchList>
                ) {
                    if (response.body()!!.status == 1) {
                        countDe=0
                        //  watchList.removeAt(position)
                        apiCallGetBasketById()
//                        watchListAdapter.notifyItemRemoved(position)
//                        watchListAdapter.notifyDataSetChanged()
                        myToast(context as Activity, "Item deleted successfully")
                    } else {
                        when (response.code()) {
                            400 -> myToast(context as Activity, "Bad Request: Invalid data")
                            401 -> myToast(context as Activity, "Unauthorized: Please login again")
                            404 -> myToast(context as Activity, "Not Found: Item not found")
                            500 -> myToast(context as Activity, "Server Error: Try again later")
                            else -> myToast(context as Activity, "Unexpected error occurred")
                        }
                        //  handleDeletionError(position)
                    }
                }

                override fun onFailure(call: Call<ModuleDeleteWatchList>, t: Throwable) {
                    // handleDeletionError(position)
                    countDe++
                    if (countDe <= 3) {
                        apiDeleteBasket(id)
                    } else {
                        myToast(context as Activity, "Network Error: ${t.message}")
                    }
                }
            })
    }


}