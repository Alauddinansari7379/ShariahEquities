package com.amtech.shariahEquities.phonepesdk

import com.papayacoders.phonepesdk.models.status.CheckStatusModel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.HeaderMap
import retrofit2.http.Path

interface ApiInterface {


    @GET("pg/v1/status/{merchantId}/{merchantTransactionId}")
    suspend fun checkStatus(
        @Path("merchantId") merchantId: String,
        @Path("merchantTransactionId") merchantTransactionId: String,
        @HeaderMap headers: Map<String, String>,
        ): Response<CheckStatusModel>
}