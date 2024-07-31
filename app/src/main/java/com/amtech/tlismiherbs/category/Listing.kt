package com.amtech.tlismiherbs.category

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.amtech.tlismiherbs.Helper.AppProgressBar
import com.sellacha.tlismiherbs.R
import com.amtech.tlismiherbs.category.adapter.AdapterListing
import com.sellacha.tlismiherbs.databinding.ActivityListingBinding
import com.sellacha.tlismiherbs.home.model.DataX
import com.amtech.tlismiherbs.retrofit.ApiClient
import com.amtech.tlismiherbs.sharedpreferences.SessionManager
import com.amtech.tlismiherbs.wishlist.Wishlist
import com.example.tlismimoti.Helper.myToast
import com.example.tlismimoti.home.model.ModelProduct
import com.facebook.shimmer.ShimmerFrameLayout

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class Listing : AppCompatActivity() {
    private val binding by lazy {
        ActivityListingBinding.inflate(layoutInflater)
    }
    private lateinit var sessionManager: SessionManager
    private var context=this@Listing
    private var mainData = ArrayList<DataX>()
    var shimmerFrameLayout: ShimmerFrameLayout? = null
    var count=0
    var countNew=0
    var value=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        shimmerFrameLayout = findViewById(R.id.shimmer)
        shimmerFrameLayout!!.startShimmer()
        sessionManager= SessionManager(context)
        with(binding){
            filter.setOnClickListener {
                val bottomSheetFragment = BottomFilterFragment()
                bottomSheetFragment.show(supportFragmentManager, bottomSheetFragment.tag)
            }
            imgBack.setOnClickListener {
                onBackPressed()
            }
            linearLayout.setOnClickListener {
                startActivity(Intent(context, Wishlist::class.java))
            }

            edtSearch.addTextChangedListener { str ->
                setRecyclerViewAdapter(mainData.filter {
                    it.title != null && it.title.toString()!!.contains(str.toString(), ignoreCase = true)
                } as ArrayList<DataX>)
            }
        }
        apiCallProduct()
    }
    private fun setRecyclerViewAdapter(data: ArrayList<DataX>) {
        binding.recyclerView.apply {
            adapter = AdapterListing(context, data)
            AppProgressBar.hideLoaderDialog()

        }
    }
    private fun apiCallProduct() {
        // AppProgressBar.showLoaderDialog(requireContext())
        ApiClient.apiService.product(sessionManager.authToken!!)
            .enqueue(object : Callback<ModelProduct> {
                override fun onResponse(
                    call: Call<ModelProduct>, response: Response<ModelProduct>
                ) {
                    try {
                        if (response.code() == 200) {
                            mainData = response.body()!!.data.posts.data
                            AppProgressBar.hideLoaderDialog()

                        }
                        if (response.code() == 500) {
                            myToast(context, "Server Error")

                        } else if (response.body()!!.data.posts.data.isEmpty()) {
                            binding!!.recyclerView.adapter =
                                AdapterListing(context, response.body()!!.data.posts.data)

                            myToast(context, "No Product Found")
                            binding.shimmer.visibility = View.GONE
                            AppProgressBar.hideLoaderDialog()

                        } else {
                            binding!!.recyclerView.adapter = AdapterListing(context, response.body()!!.data.posts.data)
                           // binding!!.recyclerView.layoutManager = GridLayoutManager(context, 2)

                            binding.shimmer.visibility = View.GONE
                            AppProgressBar.hideLoaderDialog()
//                            val men = java.util.ArrayList<DataX>()
//                            val women = java.util.ArrayList<DataX>()
//                            val kids = java.util.ArrayList<DataX>()
//                            // mainData = response.body()!!.data.posts.data
//                            for (i in response.body()!!.data.posts.data) {
//                                for (i1 in i.categories) {
//                                    if (i1.name.isNotEmpty()) {
//                                        when (i1.name) {
//                                            "MEN" -> men.add(i)
//                                            "WOMEN" -> women.add(i)
//                                            "KIDS" -> kids.add(i)
//                                        }
//                                    }
//
//                                }
//                            }
//                            var mainList = java.util.ArrayList<DataX>()
//
//                            mainList = when(value){
//                                "MEN"->{
//                                    men
//                                }
//                                "WOMEN"->{
//                                    women
//                                }
//                                "KIDS"->{
//                                    kids
//                                }else->{
//                                    response.body()!!.data.posts.data
//
//                                }
//                            }
//
//                            binding!!.recyclerView.adapter = AdapterListing(context, mainList)
//                            binding!!.recyclerView.layoutManager = GridLayoutManager(context, 2)
//
//                            binding.shimmerListing.visibility = View.GONE
//                            AppProgressBar.hideLoaderDialog()


                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        myToast(context, "Something went wrong")
                        AppProgressBar.hideLoaderDialog()

                    }
                }

                override fun onFailure(call: Call<ModelProduct>, t: Throwable) {
                    //myToast(requireActivity(), "Something went wrong")
                    count++
                    if (count <= 3) {
                        Log.e("count", count.toString())
                        apiCallProduct()
                    } else {
                        myToast(this@Listing, t.message.toString())
                        AppProgressBar.hideLoaderDialog()

                    }
                    AppProgressBar.hideLoaderDialog()


                }

            })
    }

}