package com.lepitar.streaming.networkModels

import com.google.gson.annotations.SerializedName
import com.lepitar.streaming.model.AnimeListData

data class AnimeListResponse(
    @SerializedName("list") val animeList: ArrayList<AnimeListData>
)