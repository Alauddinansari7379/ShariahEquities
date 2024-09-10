package com.example.ehcf.phonepesdk

import com.amtech.shariahEquities.phonepesdk.ApiInterface
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiUtilities {


    fun getApiInterface(): ApiInterface {
        return Retrofit.Builder()
//            .baseUrl("https://api-preprod.phonepe.com/")
            .baseUrl("https://api.phonepe.com/apis/hermes/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiInterface::class.java)
    }


}