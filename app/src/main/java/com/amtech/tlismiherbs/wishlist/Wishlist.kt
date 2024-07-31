package com.amtech.tlismiherbs.wishlist

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.amtech.tlismiherbs.Helper.AppProgressBar
 import com.sellacha.tlismiherbs.R
import com.sellacha.tlismiherbs.databinding.ActivityWishlistBinding
import com.amtech.tlismiherbs.retrofit.ApiClient
import com.amtech.tlismiherbs.sharedpreferences.SessionManager
import com.amtech.tlismiherbs.wishlist.adapter.AdapterWishlist
import com.example.tlismimoti.Helper.myToast
import com.example.tlismimoti.wishlist.model.ModelWishlist
import com.facebook.shimmer.ShimmerFrameLayout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Wishlist : AppCompatActivity() {
    val binding by lazy {
        ActivityWishlistBinding.inflate(layoutInflater)
    }
    private var shimmerFrameLayout: ShimmerFrameLayout? = null

    val context=this@Wishlist
    lateinit var sessionManager: SessionManager
    var count=0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        shimmerFrameLayout = findViewById(R.id.shimmer)
        shimmerFrameLayout!!.startShimmer()
        sessionManager= SessionManager(context)
        if (sessionManager.authTokenUser!!.isNotEmpty()){
            apiCallGetWishlist()
        }else{
            myToast(this@Wishlist,"User not logedin")
        }
        binding.imgBack.setOnClickListener {
            onBackPressed()
        }

    }
    private fun apiCallGetWishlist() {
        // AppProgressBar.showLoaderDialog(context)
        ApiClient.apiService.getWishlists(sessionManager.authToken!!,sessionManager.deviceId)
            .enqueue(object : Callback<ModelWishlist> {
                override fun onResponse(
                    call: Call<ModelWishlist>, response: Response<ModelWishlist>
                ) {
                    try {
                        if (response.code() == 500) {
                            myToast(context, "Server Error")

                        } else if (response.body()!!.data.items.isEmpty()) {
                           // binding!!.recyclerView.adapter =
                             //   AdapterWishlist(context, response.body()!!.data.items)

                            myToast(context, "No Product Found")
                            binding.shimmer.visibility = View.GONE
                            AppProgressBar.hideLoaderDialog()

                        } else {
                            binding!!.recyclerView.adapter =
                                AdapterWishlist(context, response.body()!!.data.items)
//                            binding!!.recyclerView.layoutManager =
//                                GridLayoutManager(context, 2)
                            binding.qty.text= response.body()!!.data.items.size.toString()+" Items"

                            binding.shimmer.visibility = View.GONE
                            AppProgressBar.hideLoaderDialog()


                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        myToast(context, "Something went wrong")
                        AppProgressBar.hideLoaderDialog()
                        binding.shimmer.visibility = View.GONE

                    }
                }

                override fun onFailure(call: Call<ModelWishlist>, t: Throwable) {
                    count++
                    if (count <= 2) {
                        Log.e("count", count.toString())
                        apiCallGetWishlist()
                    } else {
                        myToast(context, t.message.toString())
                        AppProgressBar.hideLoaderDialog()
                        binding.shimmer.visibility = View.GONE

                    }
                    AppProgressBar.hideLoaderDialog()


                }

            })
    }

}