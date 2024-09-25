package com.amtech.shariahEquities.retrofit

import com.amtech.shariahEquities.forgotPass.model.ModelResetPass
import com.amtech.shariahEquities.fragments.model.ModelAddWatchList
import com.amtech.shariahEquities.fragments.model.ModelCompanyDetails
import com.amtech.shariahEquities.fragments.model.modelBasketList.ModelBasketListId
import com.amtech.shariahEquities.fragments.model.modelGetBasket.ModelGetBasket
import com.amtech.shariahEquities.login.model.ModelSignUp
import com.amtech.shariahEquities.login.modelLogin.ModelLogin
import com.amtech.shariahEquities.login.modelemailotp.ModelOTP
import com.amtech.shariahEquities.modelCompany.ModelCompanyList
import com.amtech.shariahEquities.notification.adapter.moduledeletewatchlist.ModuleDeleteWatchList
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


    @DELETE("delete_watchlist")
    fun deleteWatchList(
        @Query("id") id:String,
        @Query("user_id") user_id:String,
     ): Call<ModuleDeleteWatchList>

    @POST("add_basket")
    fun addBasket(
        @Query("user_id") user_id:String,
        @Query("basketname") basketname:String,
        @Query("companyid[]") companyid:String,
        @Query("description") description:String,
     ): Call<ModelAddWatchList>

    @GET("all_basket_list")
    fun allBasketList(
        @Query("user_id") user_id:String,
     ): Call<ModelGetBasket>

    @DELETE("delete_user")
    fun deleteUser(
        @Query("id") id:String,
      ): Call<ModelResetPass>

    @DELETE("delete_basket")
    fun deleteBasket(
        @Query("user_id") user_id:String,
        @Query("basketId") basketId:String,
        ): Call<ModuleDeleteWatchList>

    @DELETE("delete_basket_single")
    fun deleteBasketSingle(
        @Query("user_id") user_id:String,
        @Query("basketId") basketId:String,
        @Query("companyid") companyid:String,
        ): Call<ModuleDeleteWatchList>

    @GET("get_basket_company_list")
    fun getBasketById(
        @Query("user_id") user_id:String,
        @Query("basketId") basketId:String,
      ): Call<ModelBasketListId>

    @POST("get_company_details")
    fun getCompanyDetails(
        @Query("companyid") companyid:String,
      ): Call<ModelCompanyDetails>

    @POST("update_subscription")
    fun updateSubscription(
        @Query("id") id:String,
        @Query("value") value:String,
        @Query("start_date") start_date:String,
        @Query("end_date") end_date:String,
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