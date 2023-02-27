package com.lepitar.streaming.retrofit

import com.lepitar.streaming.networkModels.AnimeListResponse
import retrofit2.Call
import retrofit2.http.GET

interface RetrofitService {

    @GET("/list")
    fun listRequest(): Call<AnimeListResponse>

}