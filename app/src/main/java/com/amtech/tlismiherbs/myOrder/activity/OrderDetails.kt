package com.amtech.tlismiherbs.myOrder.activity

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import cn.pedant.SweetAlert.SweetAlertDialog
import com.amtech.tlismiherbs.Helper.AppProgressBar
 import com.amtech.tlismiherbs.MainActivity
import com.sellacha.tlismiherbs.R
import com.amtech.tlismiherbs.cart.CartFragment.Companion.CartItem
import com.amtech.tlismiherbs.cart.CartFragment.Companion.tax
import com.amtech.tlismiherbs.cart.CartFragment.Companion.totalAmt
import com.amtech.tlismiherbs.myOrder.adapter.AdapterOrderDetails
import com.sellacha.tlismiherbs.cart.model.ModelAddtoCart
import com.sellacha.tlismiherbs.cart.model.ModelOrderCreate
import com.sellacha.tlismiherbs.databinding.ActivityOrderDetailsBinding
import com.amtech.tlismiherbs.retrofit.ApiClient
import com.amtech.tlismiherbs.sharedpreferences.SessionManager
import com.example.tlismimoti.Helper.myToast
import com.example.tlismimoti.cart.model.ModelActiveGateway.ModelActiveGateWays
import com.example.tlismimoti.cart.model.ModelPayment.ModelPayment
import com.example.tlismimoti.cart.model.ModelState.ModelState
import com.example.tlismimoti.mainActivity.ModelDestoryCart
import com.razorpay.PaymentResultListener
import com.squareup.picasso.Picasso
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class OrderDetails : AppCompatActivity(), AdapterOrderDetails.Cart , PaymentResultListener {
    private val binding by lazy {
        ActivityOrderDetailsBinding.inflate(layoutInflater)
    }

    private var context = this@OrderDetails
    lateinit var sessionManager: SessionManager
    var count = 0
    var count1 = 0
    var count2 = 0
    var countS = 0
    var countA = 0
    private var utrNumber = ""
    private var paymentType = ""
    private var selectedState = ""
    private var selectedCity = ""
    private var imageuRL = ""
    private var keyId = ""
    val finalTotal=ArrayList<Int>()
    private var dialog: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        sessionManager= SessionManager(context)

        apiCallCartProduct()
        apiCallGetState()
        apiCallGetPaymentList()
        apiCallGetActiveGateways()

        with(binding) {
            imgBack.setOnClickListener {
                onBackPressed()
            }

            btnPlaceOrder.setOnClickListener {

                    if (edtAddress.text!!.isEmpty()) {
                        edtAddress.error = "Enter Address"
                        edtAddress.requestFocus()
                        return@setOnClickListener
                    }
//                    if (edtCity.text!!.isEmpty()) {
//                        edtCity.error = "Enter City Name"
//                        edtCity.requestFocus()
//                        return@setOnClickListener
//                    }
//
//                    if (edtState.text!!.isEmpty()) {
//                        edtState.error = "Enter State"
//                        edtState.requestFocus()
//                        return@setOnClickListener
//                    }
                    if (edtZipCode.text!!.isEmpty()) {
                        edtZipCode.error = "Enter ZipCode"
                        edtZipCode.requestFocus()
                        return@setOnClickListener
                    }
                if (binding.radioOnline.isChecked) {

                    val view = layoutInflater.inflate(R.layout.dialog_pyment_qrcode, null)
                    dialog = Dialog(context)

                    val imgQr = view!!.findViewById<ImageView>(R.id.imgQRCode)
                    val btnSubmit = view!!.findViewById<Button>(R.id.btnSubmitDialog)
                    val edtUTR = view!!.findViewById<EditText>(R.id.edtUTR)
                    val btnCancel = view!!.findViewById<Button>(R.id.btnCancel)
                    val imgDownload = view!!.findViewById<ImageView>(R.id.imgDowanload)

                    dialog = Dialog(context)

                    if (imageuRL != null) {
                        Picasso.get().load("${sessionManager.baseURL}" + imageuRL)
                            .placeholder(R.drawable.placeholder_n)
                            .error(R.drawable.error_placeholder)
                            .into(imgQr)

                    }
                    if (view.parent != null) {
                        (view.parent as ViewGroup).removeView(view) // <- fix
                    }
                    dialog!!.setContentView(view)
                    dialog?.setCancelable(true)
                    dialog?.show()
                    btnSubmit.setOnClickListener {
                        if (edtUTR.text.isEmpty()){
                            edtUTR.error="Enter UTR Number"
                            edtUTR.requestFocus()
                            return@setOnClickListener
                        }
                        dialog?.dismiss()
//                        val intent = Intent(context as Activity, DeliveryAddress::class.java)
//                            .putExtra("UTR", edtUTR.text.toString())
//                            .putExtra("paymentType", "1")
//                        context.startActivity(intent)
                        apiCallMakeOrder()

                    }
                    imgDownload.setOnClickListener {
                        // Check for storage permissions
                        if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(context, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
                        } else {
                            downloadAndSaveImage()
                        }
                    }

                    btnCancel.setOnClickListener {
                        dialog?.dismiss()
                    }
                } else if (binding.radioRazorPay.isChecked) {
                    startPaymentOnlineRazorPay()
                } else {
                    Toast.makeText(context, "Please Select Payment Gateways", Toast.LENGTH_LONG)
                        .show()

                }

            }
        }

    }

    private fun startPaymentOnlineRazorPay() {
        val co = com.razorpay.Checkout()
        try {
            co.setKeyID(keyId)
            val options = JSONObject()
            options.put("name", "Tlismi Herbs")
            options.put("description", "Payment Description")
            //You can omit the image option to fetch the image from the dashboard
            options.put(
                "image",
                "https://tlismimoti.com/uploads/709/logo.png"
            )
            options.put("theme.color", "#04064c");
            options.put("currency", "INR");
             options.put("amount", totalAmt.toInt() * 100)//pass amount in currency subunits
           // options.put("amount", 1 * 100)//pass amount in currency subunits
            val prefill = JSONObject()
            prefill.put("email", sessionManager.userEmail)
            prefill.put("contact", sessionManager.userMobile)
            options.put("prefill", prefill)
            co.open(this, options)

        } catch (e: Exception) {
            Toast.makeText(this, "Error in payment: " + e.message, Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }
    override fun onPaymentSuccess(p0: String?) {
        Toast.makeText(this, "Payment Successful: ", Toast.LENGTH_LONG).show()
        apiCallMakeOrder()
    }

    override fun onPaymentError(p0: Int, p1: String?) {
        Toast.makeText(this, "Payment Field ", Toast.LENGTH_LONG).show()


    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            downloadAndSaveImage()
        } else {
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    private fun downloadAndSaveImage() {
        Thread {
            try {
                val client = OkHttpClient()
                val request = Request.Builder().url("${sessionManager.baseURL}"+imageuRL).build()
                val response = client.newCall(request).execute()
                val inputStream = response.body?.byteStream()
                val bitmap = BitmapFactory.decodeStream(inputStream)

                bitmap?.let {
                    saveImageToGallery(this, it, "tilsmiQR.png")
                }

            } catch (e: IOException) {
                e.printStackTrace()
                // Toast.makeText(context, "Field to saved QRCode", Toast.LENGTH_SHORT).show()

            }
        }.start()
        Toast.makeText(context, "QRCode saved to gallery", Toast.LENGTH_SHORT).show()

    }

    private fun saveImageToGallery(context: Context, bitmap: Bitmap, fileName: String) {
        val resolver = context.contentResolver
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
        }

        val uri: Uri? = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        uri?.let {
            resolver.openOutputStream(it).use { outputStream ->
                if (outputStream != null) {
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                }
            }
        }

    }
    private fun apiCallMakeOrder() {
        AppProgressBar.showLoaderDialog(context)
        ApiClient.apiService.makeOrder(
            sessionManager.authToken!!,
            "1",
            "1",
            "",
            "",
            paymentType,
            "2",
            sessionManager.userName,
            sessionManager.userEmail,
            "",
            "",
            binding.edtAddress.text.toString() + " " + selectedCity,
            binding.edtZipCode.text.toString(),
            selectedState,
            sessionManager.deviceId,
            totalAmt,
            "00",
            "00",
            utrNumber,

            )
            .enqueue(object : Callback<ModelOrderCreate> {
                @SuppressLint("SetTextI18n")
                override fun onResponse(
                    call: Call<ModelOrderCreate>, response: Response<ModelOrderCreate>
                ) {
                    try {
                        if (response.code() == 500) {
                            myToast(context, "Server Error")
                            AppProgressBar.hideLoaderDialog()

                        } else if (response.code() == 404) {
                            myToast(context, "Something went wrong")
                            AppProgressBar.hideLoaderDialog()
                        } else if (!response.body()!!.data.contains("Order Created")) {
                            myToast(context, response.body()!!.data)
                            AppProgressBar.hideLoaderDialog()
                        } else {
                            myToast(context, response.body()!!.data)
                            binding.appCompatTextView2.text = "Order placed"
                            AppProgressBar.hideLoaderDialog()
                            apiCallDestroyCart()

                            SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE)
                                .setTitleText("Order placed")
                                .setConfirmText("ok")
                                .showCancelButton(true)
                                .setConfirmClickListener { sDialog ->
                                    sDialog.cancel()
                                    val intent =
                                        Intent(applicationContext, MainActivity::class.java)
                                    intent.flags =
                                        Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                                    finish()
                                    startActivity(intent)
                                }
                                .show()

                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        myToast(context, "Something went wrong")
                        AppProgressBar.hideLoaderDialog()
                    }
                }

                override fun onFailure(call: Call<ModelOrderCreate>, t: Throwable) {
                    count++
                    if (count <= 2) {
                        apiCallMakeOrder()
                    } else {
                        myToast(context, "Something went wrong")
                        AppProgressBar.hideLoaderDialog()

                    }
                    AppProgressBar.hideLoaderDialog()


                }

            })
    }

    private fun apiCallGetState() {
        AppProgressBar.showLoaderDialog(context)

        ApiClient.apiService.getState()
            .enqueue(object : Callback<ModelState> {
                override fun onResponse(call: Call<ModelState>, response: Response<ModelState>) {
                    try {
                        val list = response.body()?.data?.posts
                        if (list != null) {
                            // Create an array of state names
                            val items = arrayOfNulls<String>(list.size)
                            for (i in list.indices) {
                                items[i] = list[i].name
                            }

                            // Create an ArrayAdapter using the state names
                            val adapter: ArrayAdapter<String?> = ArrayAdapter(
                                context, R.layout.simple_list_item_1, items
                            )

                            // Set the adapter to the spinner
                            binding.spinnerState.adapter = adapter

                            // Set the default selection
//                            val relationId = "Some default relation id" // Replace with your default relation id if available
//                            binding.spinnerState.setSelection(items.indexOf(relationId))

                            // Hide the loader dialog
                            AppProgressBar.hideLoaderDialog()

                            // Set the item selected listener for the spinner
                            binding.spinnerState.onItemSelectedListener =
                                object : AdapterView.OnItemSelectedListener {
                                    @SuppressLint("SuspiciousIndentation")
                                    override fun onItemSelected(
                                        adapterView: AdapterView<*>?,
                                        view: View?,
                                        position: Int,
                                        id: Long
                                    ) {
                                        selectedState = list[position].name
                                        val id = list[position].id
                                        apiCallGetCity(id.toString())


                                        // Handle the selected state id
                                        // Toast.makeText(context, "Selected State ID: $selectedStateId", Toast.LENGTH_SHORT).show()
                                    }

                                    override fun onNothingSelected(adapterView: AdapterView<*>?) {
                                        // Handle the case when nothing is selected, if needed
                                    }
                                }
                        } else {
                            // Handle the case when list is null
                            myToast(context, "No data found")
                            AppProgressBar.hideLoaderDialog()
                        }
                    } catch (e: Exception) {
                        myToast(context, "Something went wrong")
                        AppProgressBar.hideLoaderDialog()
                    }
                }

                override fun onFailure(call: Call<ModelState>, t: Throwable) {
                    AppProgressBar.hideLoaderDialog()
                    count1++
                    if (count1 <= 3) {
                        Log.e("count", count1.toString())
                        apiCallGetState()
                    } else {
                        myToast(context, t.message.toString())
                        AppProgressBar.hideLoaderDialog()
                    }
                }
            })
    }

    private fun apiCallGetCity(stateId: String) {

        ApiClient.apiService.getCity(stateId)
            .enqueue(object : Callback<ModelState> {
                override fun onResponse(call: Call<ModelState>, response: Response<ModelState>) {
                    try {
                        val list = response.body()?.data?.posts
                        if (list != null) {
                            // Create an array of state names
                            val items = arrayOfNulls<String>(list.size)
                            for (i in list.indices) {
                                items[i] = list[i].city
                            }

                            // Create an ArrayAdapter using the state names
                            val adapter: ArrayAdapter<String?> = ArrayAdapter(
                                context, R.layout.simple_list_item_1, items
                            )

                            // Set the adapter to the spinner
                            binding.spinnerCity.adapter = adapter

                            // Set the default selection
//                            val relationId = "Some default relation id" // Replace with your default relation id if available
//                            binding.spinnerState.setSelection(items.indexOf(relationId))

                            // Hide the loader dialog
                            AppProgressBar.hideLoaderDialog()

                            // Set the item selected listener for the spinner
                            binding.spinnerCity.onItemSelectedListener =
                                object : AdapterView.OnItemSelectedListener {
                                    override fun onItemSelected(
                                        adapterView: AdapterView<*>?,
                                        view: View?,
                                        position: Int,
                                        id: Long
                                    ) {
                                        selectedCity = list[position].city
                                        // Handle the selected state id
                                        // Toast.makeText(context, "Selected State ID: $selectedStateId", Toast.LENGTH_SHORT).show()
                                    }

                                    override fun onNothingSelected(adapterView: AdapterView<*>?) {
                                        // Handle the case when nothing is selected, if needed
                                    }
                                }
                        } else {
                            // Handle the case when list is null
                            myToast(context, "No data found")
                            AppProgressBar.hideLoaderDialog()
                        }
                    } catch (e: Exception) {
                        myToast(context, "Something went wrong")
                        AppProgressBar.hideLoaderDialog()
                    }
                }

                override fun onFailure(call: Call<ModelState>, t: Throwable) {
                    AppProgressBar.hideLoaderDialog()
                    count2++
                    if (count2 <= 3) {
                        Log.e("count", count2.toString())
                        apiCallGetCity(stateId)
                    } else {
                        myToast(context, t.message.toString())
                        AppProgressBar.hideLoaderDialog()
                    }
                }
            })
    }

    private fun apiCallDestroyCart() {
        // AppProgressBar.showLoaderDialog(context)
        ApiClient.apiService.destroyCart(
            sessionManager.authToken,
            sessionManager.deviceId.toString()
        )
            .enqueue(object : Callback<ModelDestoryCart> {
                override fun onResponse(
                    call: Call<ModelDestoryCart>, response: Response<ModelDestoryCart>
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
//                            myToast(context, "All Item Deleted")
//                            AppProgressBar.hideLoaderDialog()
//                            val intent = Intent(this@MainActivity, MainActivity::class.java)
//                            intent.flags =
//                                Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
//                            finish()
//                            startActivity(intent)
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
                    //myToast(requireActivity(), "Something went wrong")
                    count++
                    if (count <= 2) {
                        Log.e("count", count.toString())
                        apiCallDestroyCart()
                    } else {
                        myToast(context, t.message.toString())
                        AppProgressBar.hideLoaderDialog()

                    }
                    // AppProgressBar.hideLoaderDialog()


                }

            })
    }
    private fun apiCallCartProduct() {
        // AppProgressBar.showLoaderDialog(requireContext())
        ApiClient.apiService.getCart(sessionManager.authToken!!,sessionManager.deviceId,sessionManager.randomKey,"cart")
            .enqueue(object : Callback<ModelAddtoCart> {
                @SuppressLint("SetTextI18n")
                override fun onResponse(
                    call: Call<ModelAddtoCart>, response: Response<ModelAddtoCart>
                ) {
                    try {

                        if (response.code() == 500) {
                            myToast(context, "Server Error")
                            binding.shimmer.visibility = View.GONE
                            AppProgressBar.hideLoaderDialog()


                        } else if (response.body()!!.data.items.size == 0) {
                            binding!!.recyclerView.adapter =
                                AdapterOrderDetails(context, response.body()!!.data.items, this@OrderDetails)

                            myToast(context, "No Item Found")
                            binding.shimmer.visibility = View.GONE
                            CartItem ="0"
                            binding.tvTotalPrice.text=""
                            //   binding.appCompatTextView.text="0 items"
                            AppProgressBar.hideLoaderDialog()

                        } else {
                            binding!!.recyclerView.adapter =
                                AdapterOrderDetails(context, response.body()!!.data.items, this@OrderDetails)

                            CartItem =""
                            finalTotal.clear()
                            // binding.shimmer.visibility = View.GONE
                            for (i in response.body()!!.data.items) {

                                finalTotal.add(i.qty.toInt()*i.price.toInt())
                                if (i.tax!=null){
                                    tax =i.tax
                                }

                            }
                            totalAmt= finalTotal.sum().toString()

                            binding.tvTotalPrice.text = "₹" + finalTotal.sum()
                            // binding.appCompatTextView.text = response.body()!!.data.items.size.toString() + " items"
                            binding.shimmer.visibility = View.GONE
//
//                            try {
//                                if (tax.isEmpty()) {
//                                    tax = "0"
//                                }
//                                binding.totalAmt.text = "₹$totalAmt"
////            binding.deliveryFee.text = "₹00"
//                                binding.tvGSTRate.text = "GST Rate $tax%"
//                                val gST = totalAmt.toInt() * (tax.toDouble() / 100)
//                                binding.tvgst.text = "₹${gST.toFloat()}"
//                                val subTotal = totalAmt.toInt() - gST
//                                binding.subTotal.text = subTotal.toString()
//                            } catch (e: Exception) {
//                                e.printStackTrace()
//                            }
                            AppProgressBar.hideLoaderDialog()


                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        binding.shimmer.visibility = View.GONE
                    }
                }

                override fun onFailure(call: Call<ModelAddtoCart>, t: Throwable) {
                    count++
                    if (count <= 3) {
                        apiCallCartProduct()
                    } else {
                        myToast(context, "Something went wrong")
                        AppProgressBar.hideLoaderDialog()
                        binding.shimmer.visibility = View.GONE


                    }
                    AppProgressBar.hideLoaderDialog()
                    binding.shimmer.visibility = View.GONE



                }

            })
    }
    private fun apiCallGetPaymentList() {
        // AppProgressBar.showLoaderDialog(requireContext())

        ApiClient.apiService.paymentList(sessionManager.authToken)
            .enqueue(object : Callback<ModelPayment> {
                @SuppressLint("LogNotTimber")
                override fun onResponse(
                    call: Call<ModelPayment>, response: Response<ModelPayment>
                ) {
                    try {
                        if (response.code() == 500) {
                            myToast(context, "Server Error")
                            AppProgressBar.hideLoaderDialog()

                        } else if (response.body()!!.data.posts.isEmpty()) {
                            AppProgressBar.hideLoaderDialog()
                        } else {
                            var data = ""
                            for (i in response.body()!!.data.cod) {
                                data = i.active_getway.content
                            }
                            val jsonString = """
                             ${data}
                            """.trimIndent()

                            val jsonObject = JSONObject(jsonString)
                            imageuRL = jsonObject.getString("file_name")


                            println("File Name: $imageuRL")

                            AppProgressBar.hideLoaderDialog()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onFailure(call: Call<ModelPayment>, t: Throwable) {
                    countS++
                    if (countS <= 2) {
                        Log.e("count", countS.toString())
                        apiCallGetPaymentList()
                    } else {
                        myToast(context, t.message.toString())
                        AppProgressBar.hideLoaderDialog()

                    }

                    // AppProgressBar.hideLoaderDialog()


                }

            })
    }

    private fun apiCallGetActiveGateways() {
        // AppProgressBar.showLoaderDialog(requireContext())

        ApiClient.apiService.activeGateways(sessionManager.authToken)
            .enqueue(object : Callback<ModelActiveGateWays> {
                @SuppressLint("LogNotTimber")
                override fun onResponse(
                    call: Call<ModelActiveGateWays>, response: Response<ModelActiveGateWays>
                ) {
                    try {
                        if (response.code() == 500) {
                            myToast(context, "Server Error")
                            AppProgressBar.hideLoaderDialog()

                        } else if (response.body()!!.data.getways.isEmpty()) {
                            AppProgressBar.hideLoaderDialog()
                        } else {
                            var data = ""
                            for (i in response.body()!!.data.getways) {
                                data = i.content

                                countA = 0
                                val jsonString = """
                             ${data}
                            """.trimIndent()

                                val jsonObject = JSONObject(jsonString)
                                val title = jsonObject.getString("title")
                                when (title) {
                                    "QR Payment" -> binding.radioOnline.visibility = View.VISIBLE
                                    "Razorpay" -> {
                                        binding.radioRazorPay.visibility = View.VISIBLE
                                        keyId = jsonObject.getString("key_id")

                                    }

                                    "instamojo" -> binding.radioInstamojo.visibility = View.VISIBLE
                                    "paypal" -> binding.radioPaypal.visibility = View.VISIBLE
                                }


                                println("title: $title")

                            }
                            AppProgressBar.hideLoaderDialog()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onFailure(call: Call<ModelActiveGateWays>, t: Throwable) {
                    countA++
                    if (countA <= 3) {
                        Log.e("count", countA.toString())
                        apiCallGetActiveGateways()
                    } else {
                        myToast(context, t.message.toString())
                        AppProgressBar.hideLoaderDialog()

                    }

                    // AppProgressBar.hideLoaderDialog()


                }

            })
    }


//    @Deprecated("Deprecated in Java")
//    override fun onBackPressed() {
//        super.onBackPressed()
//        val intent = Intent(applicationContext, MainActivity::class.java)
//        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
//        finish()
//        startActivity(intent)
//    }

    override fun addToCart(toString: String) {

    }

    override fun removeToCart(toString: String) {
     }
}