package com.lepitar.streaming.retrofit

import com.lepitar.streaming.BuildConfig
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitManager {
    private const val baseURL = BuildConfig.ADDR

    //retrofit 클라이언트 구성
    private val retrofit = Retrofit.Builder()
        .baseUrl(baseURL)
        .addConverterFactory(ScalarsConverterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .client(OkHttpClient.Builder().connectTimeout(10, TimeUnit.SECONDS).build())
        .build()

    val retrofitClient: RetrofitService by lazy {
        retrofit.create(RetrofitService::class.java)
    }
}