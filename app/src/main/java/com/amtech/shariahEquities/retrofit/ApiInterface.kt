package com.amtech.shariahEquities.retrofit

import com.amtech.shariahEquities.forgotPass.model.ModelResetPass
import com.amtech.shariahEquities.fragments.model.ModelAddWatchList
import com.amtech.shariahEquities.login.model.ModelSignUp
import com.amtech.shariahEquities.login.modelLogin.ModelLogin
import com.amtech.shariahEquities.login.modelemailotp.ModelOTP
import com.amtech.shariahEquities.modelCompany.ModelCompanyList
import com.amtech.shariahEquities.notification.modelwatchlist.ModelWatchList
import retrofit2.Call
import retrofit2.http.DELETE
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
    @Query("email") email: String,
    @Query("password") password: String,
    ): Call<ModelLogin>

    @GET("get_profile")
    fun getProfile(
    @Query("id") id: String,
     ): Call<ModelSignUp>


    @GET("get_watch_list")
    fun getWatchList(
    @Query("user_id") id: String,
     ): Call<ModelWatchList>

    @POST("SendOTP")
    fun sendOTP(
    @Query("email") email: String,
    @Query("type") type: String,
     ): Call<ModelOTP>


    @POST("reset_password")
    fun resetPass(
    @Query("email") id: String,
    @Query("password") password: String,
     ): Call<ModelResetPass>


    @POST("get_company_list")
    fun getCompanyList(
     ): Call<ModelCompanyList>

    @POST("create_watchlist")
    fun createWatchlist(
        @Query("user_id") user_id:String,
        @Query("company_id") company_id:String,
     ): Call<ModelAddWatchList>

    @DELETE("delete_user")
    fun deleteUser(
        @Query("id") id:String,
      ): Call<ModelResetPass>

//
//    @POST("login-user")
//    fun loginUser(
//        @Query("email") email: String,
//        @Query("password") password: String,
//        @Query("device_id") device_id: String,
//        @Query("device_type") device_type: String,
//    ): Call<ModelLogin>




}