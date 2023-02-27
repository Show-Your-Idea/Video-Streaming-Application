package com.lepitar.streaming

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.lepitar.streaming.adapter.EpisodeAdapter
import com.lepitar.streaming.adapter.SeasonAdapter
import com.lepitar.streaming.databinding.ActivityMainBinding
import com.lepitar.streaming.model.EpisodeData
import com.lepitar.streaming.model.SeasonData
import com.lepitar.streaming.networkModels.AnimeListResponse
import com.lepitar.streaming.retrofit.RetrofitManager.retrofitClient
import com.lepitar.streaming.util.VerticalSpaceItemDecoration
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    private var seasonItemList = ArrayList<SeasonData>()
    private var episodeItemList = ArrayList<EpisodeData>()
    private var hashMap = HashMap<String, ArrayList<EpisodeData>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val episodeVerticalSpace = VerticalSpaceItemDecoration(50)
        binding.episodeList.addItemDecoration(episodeVerticalSpace)

        initAnimeList()

    }

    private fun initAnimeList() {
        retrofitClient.listRequest().enqueue(object : Callback<AnimeListResponse> {
            override fun onResponse(
                call: Call<AnimeListResponse>,
                response: Response<AnimeListResponse>
            ) {
                response.body()?.let { it ->
                    for (item in it.animeList) {
                        val season = seasonToKorean(item.season)
                        if (hashMap[season] == null) {
                            hashMap[season] = arrayListOf(EpisodeData(item.episode, "나의 히어로 아카데미아 $season ${item.episode}화",
                                "${BuildConfig.ADDR}/video?season=${item.season}&episode=${item.episode}"))
                        } else {
                            hashMap[season]!!.add(EpisodeData(item.episode, "나의 히어로 아카데미아 $season ${item.episode}화",
                                "${BuildConfig.ADDR}/video?season=${item.season}&episode=${item.episode}"))
                        }
                    }
                    hashMap.keys.sorted().forEach {
                        seasonItemList.add(SeasonData(it))
                    }

                    episodeItemList = hashMap[hashMap.keys.sorted()[0]]!!
                    initSeason()
                    initEpisode()
                }
            }

            override fun onFailure(call: Call<AnimeListResponse>, t: Throwable) {
                Toast.makeText(applicationContext, "목록을 불러오는데 오류가 발생하였습니다.", Toast.LENGTH_SHORT).show()
                binding.logText.text = t.toString()
                Log.e("ANIME PARSING", t.toString())
            }
        })
    }

    private fun initEpisode() {
        binding.apply {
            episodeList.adapter = EpisodeAdapter(applicationContext, episodeItemList.apply {
                sortBy { it.episode }
            })
        }
    }

    private fun seasonToKorean(season: String): String {
        val replace =  season.replace("S", "")
        return if (replace == "p") {
            "극장판"
        } else {
            replace.plus("기")
        }
    }

    fun reloadEpisode(position: Int) {
        episodeItemList = hashMap[hashMap.keys.sorted()[position]]!!
        initEpisode()
    }

    private fun initSeason() {
        binding.apply {
            seasonList.itemAnimator = null
            seasonList.adapter = SeasonAdapter(applicationContext, seasonItemList, this@MainActivity)
        }
    }
}