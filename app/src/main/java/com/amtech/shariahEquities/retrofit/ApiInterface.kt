package com.amtech.shariahEquities.retrofit

import com.amtech.shariahEquities.forgotPass.model.ModelResetPass
import com.amtech.shariahEquities.login.model.ModelSignUp
import com.amtech.shariahEquities.login.modelLogin.ModelLogin
import com.amtech.shariahEquities.login.modelemailotp.ModelOTP
import com.amtech.shariahEquities.modelCompany.ModelCompanyList
import com.amtech.shariahEquities.notification.modelwatchlist.ModelWatchList
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query


interface ApiInterface {
    @POST("register")
    fun signUp(
        @Query("name") name: String,
        @Query("email") email: String,
         @Query("mobile_number") mobile_number: String,
        @Query("password") password: String,
    ): Call<ModelSignUp>

    @POST("login")
    fun login(
    @Query("mobile_number") mobile_number: String,
    @Query("password") password: String,
    ): Call<ModelLogin>

    @GET("get_profile")
    fun getProfile(
    @Query("id") id: String,
     ): Call<ModelSignUp>


    @GET("get_watch_list")
    fun getWatchList(
    @Query("id") id: String,
     ): Call<ModelWatchList>

    @POST("SendOTP")
    fun sendOTP(
    @Query("email") email: String,
     ): Call<ModelOTP>


    @POST("reset_password")
    fun resetPass(
    @Query("id") id: String,
    @Query("password") password: String,
     ): Call<ModelResetPass>


    @POST("get_company_list")
    fun getCompanyList(
     ): Call<ModelCompanyList>

//
//    @POST("login-user")
//    fun loginUser(
//        @Query("email") email: String,
//        @Query("password") password: String,
//        @Query("device_id") device_id: String,
//        @Query("device_type") device_type: String,
//    ): Call<ModelLogin>




}