package com.amtech.shariahEquities.retrofit

import com.amtech.shariahEquities.login.model.ModelSignUp
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
//Alauddin
    @POST("login")
    fun login(
    @Query("mobile_number") mobile_number: String,
    @Query("password") password: String,
    ): Call<ModelSignUp>
///somenath
    @GET("get_profile")
    fun getProfile(
    @Query("id") id: String,
     ): Call<ModelSignUp>
//
//    @POST("login-user")
//    fun loginUser(
//        @Query("email") email: String,
//        @Query("password") password: String,
//        @Query("device_id") device_id: String,
//        @Query("device_type") device_type: String,
//    ): Call<ModelLogin>




}