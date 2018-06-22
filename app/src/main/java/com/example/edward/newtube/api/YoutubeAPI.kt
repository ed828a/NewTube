package com.example.edward.newtube.api

import android.util.Log
import com.example.edward.newtube.model.SearchVideoResponse
import com.example.edward.newtube.util.API_KEY
import com.example.edward.newtube.util.*
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query


/**
 * Created by Edward on 6/22/2018.
 */
interface YoutubeAPI {

    @GET("search")
    fun searchVideo(@Query("q") query: String = "",
                    @Query("pageToken") pageToken: String = "",
                    @Query("part") part: String = "snippet",
                    @Query("maxResults") maxResults: String = "$NETWORK_PAGE_SIZE",
                    @Query("type") type: String = "video",
                    @Query("key") key: String = API_KEY): Call<SearchVideoResponse>

    @GET("search")
    fun getRelatedVideos(@Query("relatedToVideoId") relatedToVideoId: String = "",
                         @Query("pageToken") pageToken: String = "",
                         @Query("part") part: String = "snippet",
                         @Query("maxResults") maxResults: String = "$NETWORK_PAGE_SIZE",
                         @Query("type") type: String = "video",
                         @Query("key") key: String = API_KEY): Call<SearchVideoResponse>

    companion object {

        fun createAPI(): YoutubeAPI = create(HttpUrl.parse(YOUTUBE_BASE_URL)!!)

        private fun create(httpUrl: HttpUrl): YoutubeAPI {
            val logger = HttpLoggingInterceptor(HttpLoggingInterceptor.Logger {
                Log.d("SearchVideoAPI", it)
            })

            logger.level = HttpLoggingInterceptor.Level.BASIC

            val okHttpClient = OkHttpClient.Builder().addInterceptor(logger).build()

            return Retrofit.Builder()
                    .baseUrl(httpUrl)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(YoutubeAPI::class.java)
        }
    }
}