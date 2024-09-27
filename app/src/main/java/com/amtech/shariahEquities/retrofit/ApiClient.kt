package com.amtech.shariahEquities.retrofit

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {
       private const val BASE_URL = "https://ehcf.in/api/users/"
     private var retrofit: Retrofit? = null
    private val client: Retrofit?
        get() {
            val interceptor = HttpLoggingInterceptor()
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
            val apiClient: OkHttpClient = OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .connectTimeout(30, TimeUnit.SECONDS)
                .retryOnConnectionFailure(false)
                .build()
            val gson = GsonBuilder().setLenient().create()
            if (retrofit == null) {
                retrofit = Retrofit.Builder().baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .client(apiClient)
                    .build()
            }
            return retrofit
        }
    val apiService: ApiInterface
        get() = client!!.create(ApiInterface::class.java)


}
