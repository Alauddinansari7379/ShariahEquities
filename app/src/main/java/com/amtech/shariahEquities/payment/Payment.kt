package com.amtech.shariahEquities.payment

import android.content.Intent
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.ehcf.phonepesdk.ApiUtilities
import com.phonepe.intent.sdk.api.B2BPGRequestBuilder
import com.phonepe.intent.sdk.api.PhonePe
import com.phonepe.intent.sdk.api.PhonePeInitException
import com.phonepe.intent.sdk.api.models.PhonePeEnvironment
import com.sellacha.tlismiherbs.databinding.ActivityPaymentBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.nio.charset.Charset
import java.security.MessageDigest


class Payment : AppCompatActivity() {
    val binding by lazy { ActivityPaymentBinding.inflate(layoutInflater) }
    val context = this@Payment
    var MERCHANT_TID = ""
    var apiEndPoint = "/pg/v1/pay"

    //  val salt = "099eb0cd-02cf-4e2a-8aca-3e6c6aff0399" // salt key
  //  val salt = "31f2f717-3d37-4d52-a36c-51f54c04c664" // salt key
    val salt = "53f3c71e-6a8a-4e77-a9d1-2ca3c37230bc" // salt key
   // val MERCHANT_ID = "M22NH1V8TQ8WX"  // Merhcant id
    val MERCHANT_ID = "SHARIAONLINE"  // Merhcant id

    val BASE_URL = "https://api-preprod.phonepe.com/"


//    var apiEndPoint = "/pg/v1/pay"
//    val salt = "099eb0cd-02cf-4e2a-8aca-3e6c6aff0399" // salt key
//    val MERCHANT_ID = "PGTESTPAYUAT"  // Merhcant id
//    val MERCHANT_TID = "txnId"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
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


            binding.btnPayNowYear.setOnClickListener {
                payment(5999)
            }
            binding.btnPayNowMonth.setOnClickListener {
                payment(599)
            }
        }
    }//Shariah Equities

    private fun payment(amount:Int) {
        // Generate a unique transaction ID for each payment.
        val MERCHANT_TID = System.currentTimeMillis().toString()

        // Prepare the data to be sent to PhonePe
        val data = JSONObject().apply {
            put("merchantTransactionId", MERCHANT_TID) // Unique transaction ID
            put("merchantId", MERCHANT_ID)  // Your merchant ID
            put("amount", 1 * 100)  // Amount in paisa (1 INR = 100 paisa)
            put("mobileNumber", "7379452259")  // Optional: Customer's mobile number
            put("callbackUrl", "https://webhook.site/f3ed82b0-b1f0-484b-86ba-6ca4d52f230b")  // Change to your actual callback URL in production
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
            checkStatus()
        }
    }


    private fun checkStatus() {
        val xVerify = sha256("/pg/v1/status/$MERCHANT_ID/${MERCHANT_TID}${salt}") + "###1"

        Log.d("phonepe", "xverify : $xVerify")

        val headers = mapOf(
            "Content-Type" to "application/json",
            "X-VERIFY" to xVerify,
            "X-MERCHANT-ID" to MERCHANT_ID,
        )

        lifecycleScope.launch(Dispatchers.IO) {
            val res = ApiUtilities.getApiInterface().checkStatus(
                MERCHANT_ID,
                MERCHANT_TID.toString(), headers
            )
            withContext(Dispatchers.Main) {
                Log.d("phonepe", "APIResponse${res.body()}")

                if (res.body() != null && res.body()!!.success) {
                    Log.d("phonepe", "onCreate: success")
                    Toast.makeText(context, res.body()!!.message, Toast.LENGTH_SHORT)
                        .show()
                    if (res.body()!!.code == "PAYMENT_SUCCESS") {
                        Toast.makeText(context, res.body()!!.message, Toast.LENGTH_SHORT)

                    }

                }
            }
        }


    }

    private fun sha256(input: String): String {
        val bytes = input.toByteArray(Charsets.UTF_8)
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return digest.fold("") { str, it -> str + "%02x".format(it) }
    }
}