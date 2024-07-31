package com.amtech.tlismiherbs.category

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.amtech.tlismiherbs.Helper.AppProgressBar
 import com.sellacha.tlismiherbs.R
import com.sellacha.tlismiherbs.databinding.ActivityDetailsBinding
import com.amtech.tlismiherbs.retrofit.ApiClient
import com.amtech.tlismiherbs.sharedpreferences.SessionManager
import com.example.tlismimoti.Helper.myToast
import com.example.tlismimoti.Helper.vibrateOnce
import com.example.tlismimoti.listing.model.ModelProductDetial
import com.example.tlismimoti.listing.model.cartModel.ModelCart
import com.example.tlismimoti.mainActivity.ModelDestoryCart
import com.sellacha.tlismiherbs.wishlist.model.ModelWishListId
import com.example.tlismimoti.wishlist.model.ModelWishlist
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import java.lang.String
import retrofit2.Response

class Details : AppCompatActivity() {
    private val binding by lazy {
        ActivityDetailsBinding.inflate(layoutInflater)
    }
    private var context = this@Details
     private lateinit var sessionManager: SessionManager
     var productId = ""
    private var itemCount = 1
    private var count = 0
    private var countwishadd = 0
    private var countPro = 0
    private var countCart = 0
    private var countWish = 0
    private var countWishRe = 0
    var value = ""
    var price = ""
    var wishlistId = ""
    var productTitle = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        sessionManager= SessionManager(context)
        productId = intent.getStringExtra("id").toString()
        value = intent.getStringExtra("value").toString()
        Log.e("value", value)

        Log.e("productId", productId)
        with(binding){
            imgBack.setOnClickListener {
                onBackPressed()
            }
            layoutPlush.setOnClickListener {
                vibrateOnce(context)
                itemCount++
                qty.text = String.valueOf(itemCount)
            }

            layoutMinus.setOnClickListener {
                if (itemCount != 1) {
                    if (itemCount != 0) {
                        itemCount--
                        vibrateOnce(context)
                        qty.text = String.valueOf(itemCount)
                    }
                }
            }
            btnAddToCart.setOnClickListener {
                apiCallAddToCart()
            }
            wishList.setOnClickListener {
                if (sessionManager.authTokenUser!!.isNotEmpty()) {
                    apiCallAddToWishList()
                } else {
                    myToast(context, "Please Login first")
                }
            }
            wishListRed.setOnClickListener {
                apiCallRemoveToWishList()
            }
        }


        apiCallProductDetail()
         apiCallGetWishlist()
    }

    private fun apiCallProductDetail() {
        AppProgressBar.showLoaderDialog(context)
        ApiClient.apiService.getProductDetail(sessionManager.authToken, productId)
            .enqueue(object : Callback<ModelProductDetial> {
                override fun onResponse(
                    call: Call<ModelProductDetial>, response: Response<ModelProductDetial>
                ) {
                    try {
//                        if (response.code() == 200) {
//                            mainData = response.body()!!.data.info.categories
//                            AppProgressBar.hideLoaderDialog()
//
//                        }
                        if (response.code() == 500) {
                            myToast(context, "Server Error")
                            AppProgressBar.hideLoaderDialog()
                        } else if (response.code() == 404) {
                            myToast(context, "Something went wrong")
                            AppProgressBar.hideLoaderDialog()

                        } else {
                            productTitle = response.body()!!.data.info.title
                            binding.tvTitle.text = response.body()!!.data.info.title
                            binding.tvPrice.text = "₹" + response.body()!!.data.info.price.price.toString()
                            price =  response.body()!!.data.info.price.price.toString()
                            countPro=0
                            for (i in response.body()!!.data.info.medias) {
                                Log.e("image", "https" + i.name)
                                Picasso.get().load("${sessionManager.baseURL + i.name}")
                                    .networkPolicy(
                                        NetworkPolicy.NO_CACHE
                                    )
                                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                                    .placeholder(R.drawable.placeholder_n).stableKey("id")
                                    .into(binding.image)
                            }


//                            binding!!.recyclerView.adapter = AdapterListing(context, response.body()!!.data.posts.data)
//                            binding!!.recyclerView.layoutManager =
//                                GridLayoutManager(context, 2)
//
//                            binding.shimmerListing.visibility = View.GONE
                            AppProgressBar.hideLoaderDialog()


                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        myToast(context, "Something went wrong")
                        AppProgressBar.hideLoaderDialog()


                    }
                }

                override fun onFailure(call: Call<ModelProductDetial>, t: Throwable) {
                    //myToast(context, "Something went wrong")

                    countPro++
                    if (countPro <= 3) {
                        Log.e("count", countPro.toString())
                        apiCallProductDetail()
                    } else {
                        myToast(context, t.message.toString())
                        AppProgressBar.hideLoaderDialog()
                        countPro=0

                    }
                    AppProgressBar.hideLoaderDialog()


                }

            })
    }

    private fun apiCallAddToCart() {
        AppProgressBar.showLoaderDialog(context)
        ApiClient.apiService.addToCart(
            sessionManager.authToken,
            productId,
            itemCount.toString(),
            sessionManager.deviceId
        )
            .enqueue(object : Callback<ModelCart> {
                override fun onResponse(
                    call: Call<ModelCart>, response: Response<ModelCart>
                ) {
                    try {
//                        if (response.code() == 200) {
//                            mainData = response.body()!!.data.info.categories
//                            AppProgressBar.hideLoaderDialog()
//
//                        }
                        if (response.code() == 500) {
                            myToast(context, "Server Error")
                            AppProgressBar.hideLoaderDialog()
                        } else if (response.code() == 404) {
                            myToast(context, "Something went wrong")
                            AppProgressBar.hideLoaderDialog()

                        } else if (response.code() == 200) {
                            myToast(context, "item Added in Cart")
                            AppProgressBar.hideLoaderDialog()
                            binding.tvTotalPrice.text= "₹"+(price.toInt()*itemCount).toString()
                            countCart=0
                        } else {
                            AppProgressBar.hideLoaderDialog()

                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        myToast(context, "Something went wrong")
                        AppProgressBar.hideLoaderDialog()


                    }
                }

                override fun onFailure(call: Call<ModelCart>, t: Throwable) {
                    //myToast(context, "Something went wrong")
                    countCart++
                    if (countCart <= 3) {
                        Log.e("count", countCart.toString())
                        apiCallAddToCart()
                    } else {
                        myToast(context, t.message.toString())
                        AppProgressBar.hideLoaderDialog()
                        countCart=0
                    }
                    // AppProgressBar.hideLoaderDialog()


                }

            })
    }

    private fun apiCallAddToWishList() {
        AppProgressBar.showLoaderDialog(context)
        ApiClient.apiService.addToWishlist(
            sessionManager.authToken,
            sessionManager.userEmail,
            productId,
            sessionManager.deviceId
        )
            .enqueue(object : Callback<ModelDestoryCart> {
                override fun onResponse(
                    call: Call<ModelDestoryCart>, response: Response<ModelDestoryCart>
                ) {
                    try {
                        if (response.code() == 500) {
                            myToast(context, "Server Error")
                            AppProgressBar.hideLoaderDialog()
                        } else if (response.code() == 404) {
                            myToast(context, "Something went wrong")
                            AppProgressBar.hideLoaderDialog()

                        } else if (response.code() == 200) {
//                            binding.wishListRed.visibility = View.VISIBLE
//                            binding.wishList.visibility = View.GONE
                            apiCallGetWishlist()
                            countWish=0
                            AppProgressBar.hideLoaderDialog()
                        } else {
                            AppProgressBar.hideLoaderDialog()

                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        myToast(context, "Something went wrong")
                        AppProgressBar.hideLoaderDialog()


                    }
                }

                override fun onFailure(call: Call<ModelDestoryCart>, t: Throwable) {
                    //myToast(context, "Something went wrong")
                    countWish++
                    if (countWish <= 3) {
                        Log.e("count", countWish.toString())
                        apiCallAddToWishList()
                    } else {
                        myToast(context, t.message.toString())
                        AppProgressBar.hideLoaderDialog()
                        countWish=0
                    }
                    // AppProgressBar.hideLoaderDialog()


                }

            })
    }

    private fun apiCallRemoveToWishList() {
        AppProgressBar.showLoaderDialog(context)
        ApiClient.apiService.removeWishlist(sessionManager.authToken, wishlistId)
            .enqueue(object : Callback<ModelDestoryCart> {
                override fun onResponse(
                    call: Call<ModelDestoryCart>, response: Response<ModelDestoryCart>
                ) {
                    try {
                        if (response.code() == 500) {
                            myToast(context, "Server Error")
                            AppProgressBar.hideLoaderDialog()
                        } else if (response.code() == 404) {
                            myToast(context, "Something went wrong")
                            AppProgressBar.hideLoaderDialog()

                        } else if (response.code() == 200) {
//                            binding.wishListRed.visibility = View.GONE
//                            binding.wishList.visibility = View.VISIBLE
                         //   apiCallGetWishlist()
                          //  myToast(context, response.body()!!.data)
                            binding.wishListRed.visibility = View.GONE
                            binding.wishList.visibility = View.VISIBLE
                            countWishRe=0
                            AppProgressBar.hideLoaderDialog()
                        } else {
                            AppProgressBar.hideLoaderDialog()

                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        myToast(context, "Something went wrong")
                        AppProgressBar.hideLoaderDialog()
                    }
                }

                override fun onFailure(call: Call<ModelDestoryCart>, t: Throwable) {
                    //myToast(context, "Something went wrong")
                    countWishRe++
                    if (countWishRe <= 3) {
                        Log.e("count", countWishRe.toString())
                        apiCallRemoveToWishList()
                    } else {
                        myToast(context, t.message.toString())
                        AppProgressBar.hideLoaderDialog()
                        countWishRe=0

                    }
                    // AppProgressBar.hideLoaderDialog()


                }

            })
    }

    private fun apiCallGetWishlist() {
        // AppProgressBar.showLoaderDialog(context)
        ApiClient.apiService.getWishlists(sessionManager.authToken!!, sessionManager.deviceId)
            .enqueue(object : Callback<ModelWishlist> {
                override fun onResponse(
                    call: Call<ModelWishlist>, response: Response<ModelWishlist>
                ) {
                    try {
                        if (response.code() == 500) {
                            myToast(context, "Server Error")

                        } else if (response.body()!!.data.items.isEmpty()) {
//                            binding!!.recyclerView.adapter = AdapterWishlist(context, response.body()!!.data.items)
//
//                            myToast(context, "No Product Found")
//                            binding.shimmerListing.visibility = View.GONE
                            AppProgressBar.hideLoaderDialog()

                        }else if (response.code() == 404) {
//                            binding!!.recyclerView.adapter = AdapterWishlist(context, response.body()!!.data.items)
//
//                            myToast(context, "No Product Found")
//                            binding.shimmerListing.visibility = View.GONE
                            AppProgressBar.hideLoaderDialog()

                        } else {
                            val wishlistData = ArrayList<ModelWishListId>()
                            for (i in response.body()!!.data.items) {
                                wishlistData.add(ModelWishListId(i.id.toString(), i.term_id,i.term_title))
                            }
                            var wishlistTerm = ""
                            for (i in wishlistData) {
                                if (i.termId == productId) {
                                    wishlistTerm = "1"
                                    binding.wishListRed.visibility = View.VISIBLE
                                    binding.wishList.visibility = View.GONE
                                }else{
                                    binding.wishListRed.visibility = View.GONE
                                    binding.wishList.visibility = View.VISIBLE
                                }
                                // val wishlistTerm= wishlistData.find { it.termId==productId
                            }
                            for (i in wishlistData){
                                if (i.termTitle.contentEquals(productTitle)) {
                                    Log.e("i.termTitle", i.termTitle.toString())
                                    wishlistId = i.id.toString()
                                }
                            }
                            countwishadd=0


                            // val wishlistId= wishlistData.find { it==productId }


                            Log.e("wishlistId", wishlistId.toString())
                            Log.e("productTitle", productTitle.toString())
                            Log.e("wishlistTerm", wishlistTerm.toString())
//                            if (wishlistTerm != "1") {
//                                binding.wishListRed.visibility = View.GONE
//                                binding.wishList.visibility = View.VISIBLE
//                            } else {
//                                binding.wishListRed.visibility = View.VISIBLE
//                                binding.wishList.visibility = View.GONE
//
//                            }
//                            binding!!.recyclerView.adapter =
//                                AdapterWishlist(context, response.body()!!.data.items)
//                            binding!!.recyclerView.layoutManager =
//                                GridLayoutManager(context, 2)
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

                override fun onFailure(call: Call<ModelWishlist>, t: Throwable) {
                    countwishadd++
                    if (countwishadd <= 3) {
                        Log.e("count", countwishadd.toString())
                        apiCallGetWishlist()
                    } else {
                        myToast(context, t.message.toString())
                        AppProgressBar.hideLoaderDialog()
                        countwishadd=0

                    }
                    AppProgressBar.hideLoaderDialog()


                }

            })
    }
}