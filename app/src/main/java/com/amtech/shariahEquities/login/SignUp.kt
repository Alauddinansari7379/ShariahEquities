package com.amtech.shariahEquities.login

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.text.method.PasswordTransformationMethod
import android.util.Patterns
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.amtech.shariahEquities.Helper.AppProgressBar
import com.amtech.shariahEquities.login.model.ModelSignUp
import com.amtech.shariahEquities.login.modelemailotp.ModelOTP
import com.sellacha.tlismiherbs.databinding.ActivitySignUpBinding
import com.amtech.shariahEquities.retrofit.ApiClient
import com.amtech.shariahEquities.sharedpreferences.SessionManager
import com.example.tlismimoti.Helper.myToast

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class SignUp : AppCompatActivity() {
    private var context = this@SignUp
    private lateinit var binding: ActivitySignUpBinding
    lateinit var sessionManager: SessionManager
    var count = 0
    var otp = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sessionManager = SessionManager(context)
        with(binding) {
            passwordToggle.setOnClickListener {
                passwordToggleOff.visibility = View.VISIBLE
                passwordToggle.visibility = View.GONE
                edtPassword.transformationMethod = PasswordTransformationMethod()
            }



            passwordToggleOff.setOnClickListener {
                passwordToggleOff.visibility = View.GONE
                passwordToggle.visibility = View.VISIBLE
                edtPassword.transformationMethod = null
                //binding.passwordEdt.transformationMethod =PasswordTransformationMethod(false)
                //binding.passwordToggle.sw
            }
            btnSignUp.setOnClickListener {

                if (edtFullName.text!!.isEmpty()) {
                    edtFullName.error = "Enter Full Name"
                    edtFullName.requestFocus()
                    return@setOnClickListener
                }

                if (edtEmail.text!!.isEmpty()) {
                    edtEmail.error = "Enter Email"
                    edtEmail.requestFocus()
                    return@setOnClickListener
                }
                if (!isEmailValid(edtEmail.text.toString())) {
                    edtEmail.error = "Enter a valid email"
                    edtEmail.requestFocus()
                    return@setOnClickListener
                }

                if (edtPhoneNumber.text!!.isEmpty()) {
                    edtPhoneNumber.error = "Enter Phone Number"
                    edtPhoneNumber.requestFocus()
                    return@setOnClickListener
                }
                if (edtPhoneNumber.text.toString().length < 10) {
                    edtPhoneNumber.error = "Enter Valid Mobile Number"
                    edtPhoneNumber.requestFocus()
                    return@setOnClickListener
                }
                if (edtPassword.text!!.isEmpty()) {
                    edtPassword.error = "Enter Password"
                    edtPassword.requestFocus()
                    return@setOnClickListener
                }
                if (edtPassword.text!!.toString().length < 8) {
                    edtPassword.error = "Enter 8 Digit Password"
                    edtPassword.requestFocus()
                    return@setOnClickListener
                }
                apiCallSignUp()
            }
            btnSendOTP.setOnClickListener {
                apiCallSendOTP()
            }

            btnVerify.setOnClickListener {
                if (otp == edtOTP.text.toString().toInt()) {
                    cardOTP.visibility = View.GONE
                    imgVerified.visibility = View.VISIBLE

                } else {
                    myToast(context, "Wrong OTP entered.")
                }
            }

            edtEmail.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                    // No action needed here
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    // Validate email every time the text changes
                    if (isEmailValid(edtEmail.text.toString())) {
                        layoutSendOTP.visibility = View.VISIBLE
                    } else {
                        layoutSendOTP.visibility = View.GONE

                    }
                }

                override fun afterTextChanged(s: Editable?) {
                    // No action needed here
                }
            })
            //apiCallCartRegisterUser()


        }
    }


    private fun isEmailValid(email: String?): Boolean {
        return !(email == null || TextUtils.isEmpty(email)) && Patterns.EMAIL_ADDRESS.matcher(email)
            .matches()
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
                                binding.cardOTP.visibility = View.VISIBLE
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

    private fun apiCallSignUp() {
        AppProgressBar.showLoaderDialog(context)
        ApiClient.apiService.signUp(
            binding.edtFullName.text.toString().trim(),
            binding.edtEmail.text.toString().trim(),
            binding.edtPhoneNumber.text.toString().trim(),
            binding.edtPassword.text.toString().trim(),

            )
            .enqueue(object : Callback<ModelSignUp> {
                @SuppressLint("SetTextI18n")
                override fun onResponse(
                    call: Call<ModelSignUp>, response: Response<ModelSignUp>
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

                override fun onFailure(call: Call<ModelSignUp>, t: Throwable) {
                    count++
                    if (count <= 2) {
                        apiCallSignUp()
                    } else {
                        myToast(context, "Something went wrong")
                        AppProgressBar.hideLoaderDialog()

                    }
                    AppProgressBar.hideLoaderDialog()


                }

            })
    }


}
