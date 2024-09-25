package com.amtech.shariahEquities.payment

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.ViewTreeObserver
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import cn.pedant.SweetAlert.SweetAlertDialog
import com.amtech.shariahEquities.Helper.AppProgressBar
import com.amtech.shariahEquities.MainActivity
import com.amtech.shariahEquities.forgotPass.model.ModelResetPass
import com.amtech.shariahEquities.payment.model.ModelCreatePayment
import com.amtech.shariahEquities.retrofit.ApiClient
import com.amtech.shariahEquities.sharedpreferences.SessionManager
import com.example.ehcf.phonepesdk.ApiUtilities
import com.example.tlismimoti.Helper.myToast
import com.phonepe.intent.sdk.api.B2BPGRequestBuilder
import com.phonepe.intent.sdk.api.PhonePe
import com.phonepe.intent.sdk.api.PhonePeInitException
import com.phonepe.intent.sdk.api.models.PhonePeEnvironment
import com.sellacha.tlismiherbs.R
import com.sellacha.tlismiherbs.databinding.ActivityPaymentBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.nio.charset.Charset
import java.security.MessageDigest
import kotlin.random.Random


class Payment : AppCompatActivity() {
    val binding by lazy { ActivityPaymentBinding.inflate(layoutInflater) }
    val context = this@Payment
    private var MERCHANT_TID = ""
    var count = 0
    var countC = 0
    var amount = 0
    private var apiEndPoint = "/pg/v1/pay"
    lateinit var sessionManager: SessionManager

    //  val salt = "099eb0cd-02cf-4e2a-8aca-3e6c6aff0399" // salt key
    //  val salt = "31f2f717-3d37-4d52-a36c-51f54c04c664" // salt key
    private val salt = "53f3c71e-6a8a-4e77-a9d1-2ca3c37230bc" // salt key

    // val MERCHANT_ID = "M22NH1V8TQ8WX"  // Merhcant id
    private val MERCHANT_ID = "SHARIAONLINE"  // Merhcant id

    val BASE_URL = "https://api-preprod.phonepe.com/"


//    var apiEndPoint = "/pg/v1/pay"
//    val salt = "099eb0cd-02cf-4e2a-8aca-3e6c6aff0399" // salt key
//    val MERCHANT_ID = "PGTESTPAYUAT"  // Merhcant id
//    val MERCHANT_TID = "txnId"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        sessionManager = SessionManager(context)
        // PhonePe.init(this@Payment, "YOUR_MERCHANT_ID")
        PhonePe.init(
            context,
            PhonePeEnvironment.RELEASE,
            MERCHANT_ID,
            ""
        )

        try {
            val upiApps = PhonePe.getUpiApps()
            Log.e("UPIAPPS", upiApps.toString())
        } catch (exception: PhonePeInitException) {
            exception.printStackTrace();
        }
        with(binding) {
            imgBack.setOnClickListener {
                onBackPressed()
            }

            radioYear.isChecked = true
            radioMonth.isChecked = false
            val colorStateList = ColorStateList.valueOf(Color.parseColor("#D8E6F1"))
            cardMonth.backgroundTintList = colorStateList
            val colorStateList1 = ColorStateList.valueOf(Color.parseColor("#D7F3C7"))
            cardYear.backgroundTintList = colorStateList1

            cardMonth.setOnClickListener {
                radioMonth.isChecked = true
                radioYear.isChecked = false
//                binding.cardYear.setCardBackgroundColor(Color.parseColor("#D8E6F1"))
//                binding.cardMonth.setCardBackgroundColor(Color.parseColor("#D7F3C7"))//green
                val colorStateList = ColorStateList.valueOf(Color.parseColor("#D7F3C7"))//green
                cardMonth.backgroundTintList = colorStateList
                val colorStateList1 = ColorStateList.valueOf(Color.parseColor("#D8E6F1"))
                cardYear.backgroundTintList = colorStateList1
            }
            cardYear.setOnClickListener {
                radioYear.isChecked = true
                radioMonth.isChecked = false
                val colorStateList = ColorStateList.valueOf(Color.parseColor("#D8E6F1"))
                cardMonth.backgroundTintList = colorStateList
                val colorStateList1 = ColorStateList.valueOf(Color.parseColor("#D7F3C7"))
                cardYear.backgroundTintList = colorStateList1

            }

//            binding.radioYear.setOnCheckedChangeListener { _, isChecked ->
//                if (isChecked) {
//                    radioMonth.isChecked=false
//                  }
//            }

//
            binding.btnPayNow.setOnClickListener {

                if (radioMonth.isChecked) {
                    amount = 599
                }
                if (radioYear.isChecked) {
                    amount = 5999
                }
                payment(amount)
            }
        }
    }//Shariah Equities

    private fun payment(amount: Int) {
        // Generate a unique transaction ID for each payment.
        MERCHANT_TID = System.currentTimeMillis().toString()

        // Prepare the data to be sent to PhonePe
        val data = JSONObject().apply {
            put("merchantTransactionId", MERCHANT_TID) // Unique transaction ID
            put("merchantId", MERCHANT_ID)  // Your merchant ID
            put("amount", 1 * 100)  // Amount in paisa (1 INR = 100 paisa)
            put("mobileNumber", sessionManager.userMobile)  // Optional: Customer's mobile number
            put("callbackUrl", "")  // Change to your actual callback URL in production
            val paymentInstrument = JSONObject().apply {
                put("type", "PAY_PAGE")
//                        put("targetApp", "com.phonepe.app")
                // Payment instrument type, should be PAY_PAGE for redirection
            }
            put("paymentInstrument", paymentInstrument)  // Add payment instrument object
            val deviceContext = JSONObject().apply {
                put("deviceOS", "ANDROID")  // Device OS context
            }
            put("deviceContext", deviceContext)  // Add device context object
        }

        // Convert the data to base64 encoding
        val payloadBase64 = Base64.encodeToString(
            data.toString().toByteArray(Charset.defaultCharset()),
            Base64.NO_WRAP
        )

        // Generate the checksum: SHA256(base64 encoded payload + apiEndPoint + salt key) + ### + salt index
        val checksum = sha256(payloadBase64 + apiEndPoint + salt) + "###1"

        // Log the values for debugging purposes (optional, can be removed later)
        Log.e("payloadBase64", "$payloadBase64")
        Log.e("checksum", "$checksum")

        // Create the payment request using the B2BPGRequestBuilder
        val b2BPGRequest = B2BPGRequestBuilder()
            .setData(payloadBase64)
            .setChecksum(checksum)
            .setUrl(apiEndPoint)
            .build()

        // Try to launch the PhonePe payment activity
        try {
            PhonePe.getImplicitIntent(context, b2BPGRequest, "")?.let {
                startActivityForResult(it, 1)  // Start the PhonePe payment activity
            }
        } catch (e: PhonePeInitException) {
            Log.e("PhonePayException", "${e.printStackTrace()}")  // Log any exception encountered
            Toast.makeText(context, "Error initializing PhonePe", Toast.LENGTH_SHORT)
                .show()  // Notify the user
        }

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1) {
            // Log the entire Intent object
            Log.e("PhonePayData", data.toString())

            // Check if the Intent has extras
            data?.extras?.let { bundle ->
                for (key in bundle.keySet()) {
                    val value = bundle.get(key)
                    Log.e("PhonePayExtras", "$key : $value")
                }
            }

            // Proceed to check payment status
            checkStatusNew()
        }
    }


    private fun checkStatusNew() {
        val merchantTransactionId = MERCHANT_TID
        val endpoint = "/pg/v1/status/$MERCHANT_ID/$merchantTransactionId"
        val xVerify = sha256("$endpoint$salt") + "###1"

        val headers = mapOf(
            "Content-Type" to "application/json",
            "X-VERIFY" to xVerify,
            "X-MERCHANT-ID" to MERCHANT_ID
        )

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = ApiUtilities.getApiInterface()
                    .checkStatus(MERCHANT_ID, merchantTransactionId, headers)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val result = response.body()
                        result?.let {
                            when (it.code) {

                                "PAYMENT_SUCCESS" -> {
                                    Toast.makeText(
                                        context,
                                        "Payment Successful",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    apiCallUpdateSubscription()
                                }

                                "PAYMENT_FAILED" -> {
                                    Toast.makeText(context, "Payment Failed", Toast.LENGTH_SHORT)
                                        .show()

                                }

                                else -> {
                                    Toast.makeText(context, "Payment Pending", Toast.LENGTH_SHORT)
                                        .show()

                                }
                            }
                            apiCallSavePaymentRec(it.code,it.data.merchantTransactionId)
                        }
                    } else {
                        Log.e("PhonePeError", "Error: ${response.errorBody()?.string()}")
                    }
                }
            } catch (e: Exception) {
                Log.e("PhonePeError", "Exception: ${e.localizedMessage}")
            }
        }
    }

    private fun sha256(input: String): String {
        val bytes = input.toByteArray(Charsets.UTF_8)
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return digest.fold("") { str, it -> str + "%02x".format(it) }
    }

    private fun apiCallUpdateSubscription() {
        AppProgressBar.showLoaderDialog(context)

        ApiClient.apiService.updateSubscription(sessionManager.id.toString(), "1")
            .enqueue(object : Callback<ModelResetPass> {
                @SuppressLint("LogNotTimber", "SetTextI18n")
                override fun onResponse(
                    call: Call<ModelResetPass>,
                    response: Response<ModelResetPass>
                ) {
                    AppProgressBar.hideLoaderDialog()

                    try {
                        if (response.code() == 500) {
                            myToast(context, "Server Error")
                            AppProgressBar.hideLoaderDialog()
                        } else if (response.code() == 404) {
                            myToast(context, "Something went wrong")
                            AppProgressBar.hideLoaderDialog()
                        } else {
                            if (response.body()!!.status == 1) {
                                sessionManager.subscribed = "1"
                                startFlowerRain()
                              //  binding.rainview.showAnimation()
                                val di = SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE)
                                di.setTitleText("Exclusive Access Unlocked!")
                                di.setContentText("You've unlocked all the best features. Enjoy!")
                                di.setConfirmText("ok")
                                di.showCancelButton(true)
                                di.setConfirmClickListener { sDialog ->
                                    sDialog.cancel()
                                    val intent = Intent(context, MainActivity::class.java)
                                    intent.flags =
                                        Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                                    startActivity(intent)
                                    context.finish()
                                }
                                di.setCancelClickListener { sDialog ->
                                    sDialog.cancel()
                                }
                                    .show()
                                di.setCancelable(false)
                            }
                            AppProgressBar.hideLoaderDialog()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        myToast(context as Activity, "Something went wrong")
                        AppProgressBar.hideLoaderDialog()

                    }
                }

                override fun onFailure(call: Call<ModelResetPass>, t: Throwable) {
                    AppProgressBar.hideLoaderDialog()
                    count++
                    if (count <= 3) {
                        apiCallUpdateSubscription()
                    } else {
                        myToast(
                            this@Payment,
                            t.message.toString()
                        )
                    }
                }
            })
    }
    private fun apiCallSavePaymentRec(paymentStatues: String, merchantTransactionId: String) {
       // AppProgressBar.showLoaderDialog(context)
        ApiClient.apiService.savePaymentRec(sessionManager.id.toString(), MERCHANT_TID,
            amount.toString(), paymentStatues, "Online", merchantTransactionId)
            .enqueue(object : Callback<ModelCreatePayment> {
                @SuppressLint("LogNotTimber", "SetTextI18n")
                override fun onResponse(
                    call: Call<ModelCreatePayment>,
                    response: Response<ModelCreatePayment>
                ) {
                    AppProgressBar.hideLoaderDialog()

                    try {
                        if (response.code() == 500) {
                            myToast(context, "Server Error")
                            AppProgressBar.hideLoaderDialog()
                        } else if (response.code() == 404) {
                            myToast(context, "Something went wrong")
                            AppProgressBar.hideLoaderDialog()
                        } else {
                            if (response.body()!!.status == 1) {

                            }
                            AppProgressBar.hideLoaderDialog()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        myToast(context as Activity, "Something went wrong")
                        AppProgressBar.hideLoaderDialog()

                    }
                }

                override fun onFailure(call: Call<ModelCreatePayment>, t: Throwable) {
                    AppProgressBar.hideLoaderDialog()
                    countC++
                    if (countC <= 3) {
                        apiCallSavePaymentRec(paymentStatues,merchantTransactionId)
                    } else {
                        myToast(
                            this@Payment,
                            t.message.toString()
                        )
                    }
                }
            })
    }
    private fun startFlowerRain() {
        binding.rainview.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                binding.rainview.viewTreeObserver.removeOnGlobalLayoutListener(this)
                for (i in 1..10) { // Change the number for more/less flowers
                    val flower = ImageView(context)
                    flower.setImageResource(R.drawable.flawer) // Add your flower drawable here

                    // Set initial position
                    flower.translationX = 5f
                    flower.translationY = 10f

                    binding.rainview.addView(flower)

                    // Animate falling
                    val animator = ObjectAnimator.ofFloat(
                        flower,
                        "translationY",
                        0f,
                        binding.rainview.height.toFloat()
                    )
                    animator.duration = Random.nextLong(1000, 3000) // Random duration
                    animator.start()

                    // Remove flower after animation ends
                    animator.addListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            binding.rainview.removeView(flower)
                        }
                    })
                }
            }
        })
    }

      fun refresh() {
        overridePendingTransition(0, 0)
        finish()
        startActivity(intent)
        overridePendingTransition(0, 0)
    }
    override fun onStop() {
        super.onStop()
       // binding.rainview.animationClear()
    }
}