package com.amtech.shariahEquities.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.amtech.shariahEquities.Helper.AppProgressBar
import com.amtech.shariahEquities.fragments.adapter.AdapterBasketList
import com.amtech.shariahEquities.fragments.adapter.FundsAdapter
import com.amtech.shariahEquities.fragments.model.modelBasketList.Company
import com.amtech.shariahEquities.fragments.model.modelBasketList.ModelBasketListId
import com.amtech.shariahEquities.retrofit.ApiClient
import com.amtech.shariahEquities.sharedpreferences.SessionManager
import com.example.tlismimoti.Helper.myToast
import com.sellacha.tlismiherbs.databinding.ActivityBasketListBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BasketsList : AppCompatActivity() {
    private val context = this@BasketsList
    private lateinit var binding: ActivityBasketListBinding
    lateinit var sessionManager: SessionManager
    var count = 0
    private var basketId = ""
    private var list = ArrayList<Company>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBasketListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sessionManager = SessionManager(context)

        basketId = intent.getStringExtra("basketId").toString()
        apiCallGetBasketByIdt()
        with(binding) {

            imgBack.setOnClickListener {
                onBackPressed()
            }
        }
    }

    private fun apiCallGetBasketByIdt() {
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
                                    adapter = AdapterBasketList(this@BasketsList, list)
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
                    if (count <= 2) {
                        apiCallGetBasketByIdt()
                    } else {
                        myToast(context as Activity, "Something went wrong")
                    }
                    AppProgressBar.hideLoaderDialog()
                }
            })
    }


}