package com.amtech.shariahEquities.forgotPass

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import cn.pedant.SweetAlert.SweetAlertDialog
import com.amtech.shariahEquities.Helper.AppProgressBar
import com.amtech.shariahEquities.Helper.AppProgressBar.dialog
import com.amtech.shariahEquities.forgotPass.model.ModelResetPass
import com.amtech.shariahEquities.login.Login
import com.amtech.shariahEquities.login.modelemailotp.ModelOTP
import com.amtech.shariahEquities.retrofit.ApiClient

import com.example.tlismimoti.Helper.myToast
import com.sellacha.tlismiherbs.R
import com.sellacha.tlismiherbs.databinding.ActivityOtpverificationBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import rezwan.pstu.cse12.youtubeonlinestatus.recievers.NetworkChangeReceiver
import xyz.teamgravity.checkinternet.CheckInternet


class OTPVerification : AppCompatActivity() {
    private lateinit var binding: ActivityOtpverificationBinding
    var progressDialog: ProgressDialog? = null
    val context = this@OTPVerification
    var phoneNumber = ""
    var id = ""
    var count = 0
    var dialog: Dialog? = null
    private var otp = 0
    var finalOTP = ""

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOtpverificationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        timeCounter()



        binding.tvMobileNumber.text = "+${binding.edtEmail.text}"
        // otpPopup(otp)

        Log.e(ContentValues.TAG, "Mobilenumber-----:---$phoneNumber")

        binding.btnVerify.setOnClickListener {
            val otp1 = binding.otpBox1.text.toString()
            val otp2 = binding.otpBox2.text.toString()
            val otp3 = binding.otpBox3.text.toString()
            val otp4 = binding.otpBox4.text.toString()
            Log.e("otp1", otp1)

            finalOTP = "$otp1$otp2$otp3$otp4"

            Log.e("finalOTP", finalOTP.toString())
            Log.e("otp", otp.toString())


            if (finalOTP.toInt() == otp) {
                changePassDialog("Change Password")

            } else {
                myToast(this@OTPVerification, "You are entered wrong OTP")
            }

        }
        binding.imgBack.setOnClickListener {
            onBackPressed()
        }
        binding.btnSend.setOnClickListener {
            if (binding.edtEmail.text!!.isEmpty()) {
                binding.edtEmail.error = "Enter Phone Number"
                binding.edtEmail.requestFocus()
                return@setOnClickListener
            }
            if (!isEmailValid(binding.edtEmail.text.toString())) {
                binding.edtEmail.error = "Enter a valid email"
                binding.edtEmail.requestFocus()
                return@setOnClickListener
            }

            apiCallSendOTP()
        }

        binding.otpBox1.addTextChangedListener {
            if (binding.otpBox1.length() == 1) {
                binding.otpBox2.requestFocus()
            }
        }
        binding.otpBox2.addTextChangedListener {
            if (binding.otpBox2.length() == 1)
                binding.otpBox3.requestFocus()
        }
        binding.otpBox3.addTextChangedListener {
            if (binding.otpBox3.length() == 1)
                binding.otpBox4.requestFocus()
        }
        binding.otpBox4.addTextChangedListener {
            if (binding.otpBox4.length() == 1)
                binding.otpBox5.requestFocus()
        }
        binding.otpBox5.addTextChangedListener {
            if (binding.otpBox5.length() == 1)
                binding.otpBox6.requestFocus()
        }
        binding.tvResend.setOnClickListener {
            binding.tvResend.setTextColor(Color.parseColor("#858284"))
            progressDialog!!.show()

            ApiClient.apiService.sendOTP(binding.edtEmail.text.toString().trim()).enqueue(object :Callback<ModelOTP>{

                override fun onResponse(
                    call: Call<ModelOTP>, response: Response<ModelOTP>
                ) {
                    // Log.e("Ala","${response.body()!!.result}")
                    Log.e("Ala","${response.body()!!.message}")
                    Log.e("Ala","${response.body()!!.status}")
                    if (response.body()!!.status==1){
                        myToast(this@OTPVerification,response.body()!!.message)
                        progressDialog!!.dismiss()
                         otp = response.body()!!.result.otp.toString().toInt()
//                        val id = response.body()!!.result.id
                        this@OTPVerification.otp =otp
                       // otpPopup(otp)
                        timeCounter()
                    }
                    else{
                        myToast(this@OTPVerification,"${response.body()!!.message}")
                        progressDialog!!.dismiss()
                    }
                }
                override fun onFailure(call: Call<ModelOTP>, t: Throwable) {
                    myToast(this@OTPVerification,"Something went wrong")
                    progressDialog!!.dismiss()

                }

            })
        }


    }
    private fun isEmailValid(email: String?): Boolean {
        return !(email == null || TextUtils.isEmpty(email)) && Patterns.EMAIL_ADDRESS.matcher(email)
            .matches()
    }
    private fun changePassDialog(s: String) {
        val view = layoutInflater.inflate(R.layout.dialog_password_change, null)
        dialog = context?.let { Dialog(it) }

        val btnChange = view!!.findViewById<Button>(R.id.btnChangeDialogPass)
        val tvDateFromDilog = view!!.findViewById<TextView>(R.id.tvDateFromDilog)
        val newPass = view!!.findViewById<EditText>(R.id.edtNewPasswordDialogPass)
        val fullName = view!!.findViewById<EditText>(R.id.edtFullNameDil)
        val mobile = view!!.findViewById<EditText>(R.id.edtMobileDil)
        val currentPass = view!!.findViewById<EditText>(R.id.edtCurrentPasswordDialogPass)
        dialog = context?.let { Dialog(it) }
        tvDateFromDilog.text = s

        if (view.parent != null) {
            (view.parent as ViewGroup).removeView(view) // <- fix
        }
        dialog!!.setContentView(view)
        dialog?.setCancelable(true)

        fullName.visibility = View.GONE
        mobile.visibility = View.GONE
        currentPass.setHint("New Password")
        newPass.setHint("Confirm Password")
        dialog?.show()
//        mobile.setText(sessionManager.userMobile)
//        fullName.setText(sessionManager.userName)

        btnChange.setOnClickListener {
            if (currentPass.text!!.isEmpty()) {
                currentPass.error = "Enter Current Password"
                currentPass.requestFocus()
                return@setOnClickListener
            }
            if (newPass.text!!.isEmpty()) {
                newPass.error = "Enter New Password"
                newPass.requestFocus()
                return@setOnClickListener
            }
            val password = newPass.text.toString().trim()
            val currentPass = currentPass.text.toString().trim()
            val name = fullName.text.toString().trim()
            val mobile = mobile.text.toString().trim()

            // apiCallUpdateSetting(name, mobile, currentPass, password)
            dialog?.dismiss()
            apiCallResetPass(currentPass,"3")
        }
    }

    private fun otpPopup(otp: String) {
        SweetAlertDialog(this@OTPVerification, SweetAlertDialog.SUCCESS_TYPE)
            .setTitleText("Your OTP is $otp")
            .setConfirmText("Ok")
            .showCancelButton(true)
            .setConfirmClickListener { sDialog ->
                sDialog.cancel()
            }
            .setCancelClickListener { sDialog ->
                sDialog.cancel()
            }
            .show()
    }

    private fun apiCallSendOTP() {
        AppProgressBar.showLoaderDialog(context)
        ApiClient.apiService.sendOTP(
            binding.edtEmail.text.toString().trim(),

            )
            .enqueue(object : Callback<ModelOTP> {
                @SuppressLint("SetTextI18n")
                override fun onResponse(
                    call: Call<ModelOTP>, response: Response<ModelOTP>
                ) {
                    try {
                        if (response.code() == 500) {
                            myToast(context, "Server Error")
                            AppProgressBar.hideLoaderDialog()


                        } else if (response.code() == 404) {
                            myToast(context, "Something went wrong")
                            AppProgressBar.hideLoaderDialog()

                        } else {
                            myToast(context, response.body()!!.message)

                            otp = response.body()!!.result.otp
                            if (otp.toString().isNotEmpty()) {
                                binding.layoutSendOTP.visibility = View.GONE
                                binding.layoutVerifyOTP.visibility = View.VISIBLE
                            }
                            AppProgressBar.hideLoaderDialog()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        myToast(context, "Something went wrong")
                        AppProgressBar.hideLoaderDialog()
                    }
                }

                override fun onFailure(call: Call<ModelOTP>, t: Throwable) {
                    count++
                    if (count <= 2) {
                        apiCallSendOTP()
                    } else {
                        myToast(context, t.message.toString())
                        AppProgressBar.hideLoaderDialog()

                    }
                    AppProgressBar.hideLoaderDialog()


                }

            })
    }
    private fun apiCallResetPass(password:String,id:String) {
        AppProgressBar.showLoaderDialog(context)
        ApiClient.apiService.resetPass(
            binding.edtEmail.text.toString().trim(),
            binding.edtEmail.text.toString().trim(),

            )
            .enqueue(object : Callback<ModelResetPass> {
                @SuppressLint("SetTextI18n")
                override fun onResponse(
                    call: Call<ModelResetPass>, response: Response<ModelResetPass>
                ) {
                    try {
                        if (response.code() == 500) {
                            myToast(context, "Server Error")
                            AppProgressBar.hideLoaderDialog()


                        } else if (response.code() == 404) {
                            myToast(context, "Something went wrong")
                            AppProgressBar.hideLoaderDialog()

                        } else {
                            myToast(context, response.body()!!.message)
                            val intent = Intent(applicationContext, Login::class.java)
                            intent.flags =
                                Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                            finish()
                            startActivity(intent)
                            AppProgressBar.hideLoaderDialog()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        myToast(context, "Something went wrong")
                        AppProgressBar.hideLoaderDialog()
                    }
                }

                override fun onFailure(call: Call<ModelResetPass>, t: Throwable) {
                    count++
                    if (count <= 2) {
                        apiCallSendOTP()
                    } else {
                        myToast(context, t.message.toString())
                        AppProgressBar.hideLoaderDialog()

                    }
                    AppProgressBar.hideLoaderDialog()


                }

            })
    }


    private fun timeCounter() {
        object : CountDownTimer(30000, 1000) {

            // Callback function, fired on regular interval
            @SuppressLint("SetTextI18n")
            override fun onTick(millisUntilFinished: Long) {
                binding.tvResend.isClickable = false
                binding.tvSecond.text =
                    "OTP Resend In : " + millisUntilFinished / 1000 + " " + " Seconds"
            }

            override fun onFinish() {
                binding.tvResend.isClickable = true
                binding.tvResend.setTextColor(Color.parseColor("#9F367A"))

            }
        }.start()
    }


    override fun onStart() {
        super.onStart()
        CheckInternet().check { connected ->
            if (connected) {

                // myToast(requireActivity(),"Connected")
            } else {
                val changeReceiver = NetworkChangeReceiver(context)
                changeReceiver.build()
                //  myToast(requireActivity(),"Check Internet")
            }
        }
    }

}