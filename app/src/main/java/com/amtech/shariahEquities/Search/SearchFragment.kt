package com.amtech.shariahEquities.Search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.sellacha.tlismiherbs.R
import com.sellacha.tlismiherbs.databinding.FragmentCartBinding
import com.amtech.shariahEquities.sharedpreferences.SessionManager

import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.bottomsheet.BottomSheetDialog

class SearchFragment : Fragment() {
    private lateinit var binding: FragmentCartBinding
    private lateinit var sessionManager: SessionManager
    var shimmerFrameLayout: ShimmerFrameLayout? = null
    val finalTotal=ArrayList<Int>()

    // private var mainData = ArrayList<Items>()
    var count = 0
    var countRe = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_cart, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentCartBinding.bind(view)
      //  shimmerFrameLayout = view.findViewById(R.id.shimmer)
//        shimmerFrameLayout!!.startShimmer()
        sessionManager = SessionManager(requireContext())

        val bottomSheetDialog = BottomSheetDialog(requireContext())
        val parentView: View = layoutInflater.inflate(R.layout.login_dialog, null)
        bottomSheetDialog.setContentView(parentView)
//        if (parentView.parent != null) {
//            (parentView.parent as ViewGroup).removeView(parentView) // <- fix
//        }
     //   apiCallCartProduct()

        // MainActivity


            //priceUndrline.paintFlags = priceUndrline.paintFlags and STRIKE_THRU_TEXT_FLAG.inv()


//        val dialog = BottomSheetDialog(requireContext())
//        val bottomSheet = layoutInflater.inflate(R.layout.bottom_sheet, null)
//
//        bottomSheet.buttonSubmit.setOnClickListener {
//            dialog.dismiss()
//        }
//
//        dialog.setContentView(bottomSheet)
//        dialog.show()

    }
    //     override fun onDestroy() {
//        super.onDestroy()
//        if (bottomSheetDialog != null && pDialog.isShowing()) {
//            pDialog.dismiss()
//        }
//    }
/*    private fun apiCallCartProduct() {
        // AppProgressBar.showLoaderDialog(requireContext())
        ApiClient.apiService.getCart(sessionManager.authToken!!, sessionManager.deviceId,sessionManager.randomKey,"cart")
            .enqueue(object : Callback<ModelAddtoCart> {
                @SuppressLint("SetTextI18n")
                override fun onResponse(
                    call: Call<ModelAddtoCart>, response: Response<ModelAddtoCart>
                ) {
                    try {

                        if (response.code() == 500) {
                            activity?.let { myToast(it, "Server Error") }
                            binding.shimmer.visibility = View.GONE
                            AppProgressBar.hideLoaderDialog()


                        } else if (response.body()!!.data.items.size == 0) {
                            binding!!.recyclerView.adapter =
                                activity?.let {
                                    AdapterCart(
                                        it,
                                        response.body()!!.data.items,
                                        this@CartFragment
                                    )
                                }
                            activity?.let { myToast(it, "No Item Found") }
                            binding.shimmer.visibility = View.GONE
                            binding.layoutMain.visibility = View.GONE
                            binding.tvNoDtaFound.visibility = View.VISIBLE
                            CartItem ="0"
                            binding.totalPrice.text=""
                            //   binding.appCompatTextView.text="0 items"
                            AppProgressBar.hideLoaderDialog()

                        } else {
                            binding!!.recyclerView.adapter =
                                activity?.let {
                                    AdapterCart(
                                        it,
                                        response.body()!!.data.items!!,

                                        this@CartFragment
                                    )
                                }
                            CartItem =""
                            finalTotal.clear()
                            // binding.shimmer.visibility = View.GONE
                            for (i in response.body()!!.data.items) {

                                finalTotal.add(i.qty.toInt()*i.price.toInt())
                                if (i.tax!=null){
                                    tax =i.tax
                                }

                            }
                            totalAmt = finalTotal.sum().toString()

                            binding.totalPrice.text = "₹" + finalTotal.sum()
                            // binding.appCompatTextView.text = response.body()!!.data.items.size.toString() + " items"
                            binding.shimmer.visibility = View.GONE

                            try {
                                if (tax.isEmpty()) {
                                    tax = "0"
                                }
                                binding.totalAmt.text = "₹$totalAmt"
//            binding.deliveryFee.text = "₹00"
                                binding.tvGSTRate.text = "GST Rate $tax%"
                                val gST = totalAmt.toInt() * (tax.toDouble() / 100)
                                binding.tvgst.text = "₹${gST.toFloat()}"
                                val subTotal = totalAmt.toInt() - gST
                                binding.subTotal.text = subTotal.toString()
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                            AppProgressBar.hideLoaderDialog()


                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        activity?.let { myToast(it, "Something went wrong") }
                        binding.shimmer.visibility = View.GONE
                    }
                }

                override fun onFailure(call: Call<ModelAddtoCart>, t: Throwable) {
                    count++
                    if (count <= 3) {
                        apiCallCartProduct()
                    } else {
                        activity?.let { myToast(it, "Something went wrong") }
                        AppProgressBar.hideLoaderDialog()
                        binding.shimmer.visibility = View.GONE


                    }
                    AppProgressBar.hideLoaderDialog()
                    binding.shimmer.visibility = View.GONE



                }

            })
    }

    override fun addToCart(productId: String, variationId: String, optionId: String) {
        AppProgressBar.showLoaderDialog(context)
        ApiClient.apiService.addToCart(
            sessionManager.authToken,
            productId,
            "1",
            sessionManager.deviceId.toString(),sessionManager.randomKey,"cart",optionId,variationId
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
                            activity?.let { myToast(it, "Server Error") }
                            AppProgressBar.hideLoaderDialog()
                        } else if (response.code() == 404) {
                            activity?.let { myToast(it, "Something went wrong") }
                            AppProgressBar.hideLoaderDialog()

                        } else if (response.code() == 200) {
                            activity?.let { myToast(it, "item Added in Cart") }
                            apiCallCartProduct()
                            AppProgressBar.hideLoaderDialog()
                        } else {
                            AppProgressBar.hideLoaderDialog()

                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        activity?.let { myToast(it, "Something went wrong") }
                        AppProgressBar.hideLoaderDialog()


                    }
                }

                override fun onFailure(call: Call<ModelCart>, t: Throwable) {
                    //myToast(activity, "Something went wrong")
                    count++
                    if (count <= 2) {
                        Log.e("count", count.toString())
                        addToCart(productId, variationId, optionId)
                    } else {
                        activity?.let { myToast(it, t.message.toString()) }
                        AppProgressBar.hideLoaderDialog()

                    }
                    // AppProgressBar.hideLoaderDialog()


                }

            })
    }

    override fun removeToCart(id: String) {
        AppProgressBar.showLoaderDialog(context)
        ApiClient.apiService.removeToCart(
            sessionManager.authToken,
            id,
            sessionManager.deviceId.toString(),sessionManager.randomKey,"cart"
        )
            .enqueue(object : Callback<ModelAddtoCart> {
                override fun onResponse(
                    call: Call<ModelAddtoCart>, response: Response<ModelAddtoCart>
                ) {
                    try {
//                        if (response.code() == 200) {
//                            mainData = response.body()!!.data.info.categories
//                            AppProgressBar.hideLoaderDialog()
//
//                        }
                        if (response.code() == 500) {
                            activity?.let { myToast(it, "Server Error") }
                            AppProgressBar.hideLoaderDialog()
                        } else if (response.code() == 404) {
                            activity?.let { myToast(it, "Something went wrong") }
                            AppProgressBar.hideLoaderDialog()

                        } else if (response.code() == 200) {
                            activity?.let { myToast(it, "item Removed from Cart") }
                            apiCallCartProduct()
                            countRe=0
                            AppProgressBar.hideLoaderDialog()
                        } else {
                            AppProgressBar.hideLoaderDialog()

                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        activity?.let { myToast(it, "Something went wrong") }
                        AppProgressBar.hideLoaderDialog()


                    }
                }

                override fun onFailure(call: Call<ModelAddtoCart>, t: Throwable) {
                    //myToast(activity, "Something went wrong")
                    countRe++
                    if (countRe <= 3) {
                        Log.e("count", countRe.toString())
                        removeToCart(id)

                    } else {
                        activity?.let { myToast(it, t.message.toString()) }
                        AppProgressBar.hideLoaderDialog()
                        countRe=0

                    }
                    // AppProgressBar.hideLoaderDialog()


                }

            })

    }*/
    companion object{
        var CartItem=""
        var totalAmt = ""
        var tax = ""
    }



}