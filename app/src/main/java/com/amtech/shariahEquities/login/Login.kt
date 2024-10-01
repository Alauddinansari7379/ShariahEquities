package com.amtech.shariahEquities.login

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.amtech.shariahEquities.Helper.AppProgressBar
import com.amtech.shariahEquities.MainActivity
import com.amtech.shariahEquities.forgotPass.OTPVerification
import com.amtech.shariahEquities.login.model.ModelSignUp
import com.amtech.shariahEquities.login.modelLogin.ModelLogin
import com.sellacha.tlismiherbs.databinding.ActivityLoginBinding
import com.amtech.shariahEquities.retrofit.ApiClient
import com.amtech.shariahEquities.sharedpreferences.SessionManager
import com.example.tlismimoti.Helper.isInternetAvailable
import com.example.tlismimoti.Helper.myToast

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Login : AppCompatActivity() {
    private val context = this@Login
    private lateinit var binding: ActivityLoginBinding
    lateinit var sessionManager: SessionManager
    var count = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sessionManager = SessionManager(context)


        Log.e("sessionManager.fcmToken", sessionManager.fcmToken.toString())

        if (sessionManager.isLogin) {
            val intent = Intent(applicationContext, MainActivity::class.java)
            intent.flags =
                Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            finish()
            startActivity(intent)
        }
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
            tvForgot.setOnClickListener {
                startActivity(Intent(this@Login, OTPVerification::class.java))
            }
            tvSignup.setOnClickListener {
                startActivity(Intent(this@Login, SignUp::class.java))
            }
            btnLogIn.setOnClickListener {

//                val intent = Intent(applicationContext, MainActivity::class.java)
//                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
//                finish()
//                startActivity(intent)
//                Log.e("FCMNewSession",fcmTokenNew)
                if (edtUserName.text!!.isEmpty()) {
                    edtUserName.error = "Enter Username"
                    edtUserName.requestFocus()
                    return@setOnClickListener
                }
                if (edtPassword.text!!.isEmpty()) {
                    edtPassword.error = "Enter Password"
                    edtPassword.requestFocus()
                    return@setOnClickListener
                }
//                if(sessionManager.fcmToken!!.isNotEmpty()){
//                    saveFCM(sessionManager.fcmToken.toString())
//                }
//                fcmTokenNew = sharedPreferences.getString(FCM_TOKEN, "").toString()
//
                login(edtUserName.text.toString().trim(), edtPassword.text.toString().trim())

            }
        }

    }

    private fun login(email: String, password: String) {
        if (!isInternetAvailable(context as Activity)) {
            myToast(
                context as Activity,
                "No internet connection. Please check your network settings."
            )
            return
        }
        AppProgressBar.showLoaderDialog(this@Login)
        ApiClient.apiService.login(
            email,
            password,
        ).enqueue(object :
            Callback<ModelLogin> {
            @SuppressLint("LogNotTimber", "LongLogTag")
            override fun onResponse(
                call: Call<ModelLogin>,
                response: Response<ModelLogin>
            ) {
                try {
                    if (response.code() == 500) {
                        myToast(this@Login, "Server Error")
                        AppProgressBar.hideLoaderDialog()
                    } else if (response.code() == 404) {
                        myToast(this@Login, "Something went wrong")
                        AppProgressBar.hideLoaderDialog()
//                    }else if (response.body()!!.data.token!!.isNotEmpty()) {
                    } else {
                        myToast(context, response.body()!!.message)
//                        sessionManager.authTokenUser = "Bearer " + response.body()!!.data.token

                        if (response.body()!!.result.status==1) {
                             sessionManager.email = response.body()!!.result.email
                            Log.e("response.body()!!.result.email",response.body()!!.result.email)
                            sessionManager.userName = response.body()!!.result.name
                            sessionManager.id = response.body()!!.result.Id
                            sessionManager.status = response.body()!!.result.status.toString()
                            sessionManager.userMobile = response.body()!!.result.mobile_number.toString()
                            sessionManager.subscribed = response.body()!!.result.IsSubscribed.toString()
                            if (response.body()!!.result.subscription_start_date!=null&&response.body()!!.result.subscription_end_date!=null){
                                sessionManager.startDate = response.body()!!.result.subscription_start_date.toString()
                                sessionManager.endDate = response.body()!!.result.subscription_end_date.toString()
                            }

                            sessionManager.isLogin = true
                            val intent = Intent(applicationContext, MainActivity::class.java)
                            intent.flags =
                                Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                            finish()
                            startActivity(intent)
                        }
                        AppProgressBar.hideLoaderDialog()

//                    } else {
//                        myToast(context, "Login failed, please try again!")
//                        AppProgressBar.hideLoaderDialog()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    myToast(context, "Something went wrong")
                    AppProgressBar.hideLoaderDialog()

                }

            }

            override fun onFailure(call: Call<ModelLogin>, t: Throwable) {
                count++
                if (count <= 2) {
                    login(email, password)
                } else {
                    myToast(context, "Something went wrong")
                    AppProgressBar.hideLoaderDialog()

                }
                AppProgressBar.hideLoaderDialog()


            }

        })
    }

}