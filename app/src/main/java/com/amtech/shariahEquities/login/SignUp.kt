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

                startActivity(Intent(context, Login::class.java))
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
            }

            edtEmail.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
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

//    private fun apiCallCartRegisterUser() {
//        AppProgressBar.showLoaderDialog(context)
//        ApiClient.apiService.registerUser(
//            sessionManager.authToken!!,
//            binding.edtFullName.text.toString().trim(),
//            binding.edtEmail.text.toString().trim(),
//            binding.edtPassword.text.toString().trim(),
//            binding.edtPhoneNumber.text.toString().trim(),
//        )
//            .enqueue(object : Callback<ModelUserSignUp> {
//                @SuppressLint("SetTextI18n")
//                override fun onResponse(
//                    call: Call<ModelUserSignUp>, response: Response<ModelUserSignUp>
//                ) {
//                    try {
//                        if (response.code() == 500) {
//                            myToast(context, "Server Error")
//                            AppProgressBar.hideLoaderDialog()
//
//
//                        } else if (response.code() == 404) {
//                            myToast(context, "Something went wrong")
//                            AppProgressBar.hideLoaderDialog()
//                        } else if (response.body()!!.data.email.isNotEmpty()) {
//                            myToast(context, "Successfully Registered")
//                                val intent = Intent(applicationContext, Login::class.java)
//                                    intent.flags =
//                                    Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
//                                    finish()
//                                    startActivity(intent)
//                            AppProgressBar.hideLoaderDialog()
//                        } else {
//                            myToast(context, response.body()!!.data.toString())
//                            AppProgressBar.hideLoaderDialog()
//                        }
//                    } catch (e: Exception) {
//                        e.printStackTrace()
//                        myToast(context, "Something went wrong")
//                        AppProgressBar.hideLoaderDialog()
//                    }
//                }
//
//                override fun onFailure(call: Call<ModelUserSignUp>, t: Throwable) {
//                    count++
//                    if (count <= 2) {
//                        apiCallCartRegisterUser()
//                    } else {
//                        myToast(context, "Something went wrong")
//                        AppProgressBar.hideLoaderDialog()
//
//                    }
//                    AppProgressBar.hideLoaderDialog()
//
//
//                }
//
//            })
//    }


}
