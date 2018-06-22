package com.example.edward.newtube.repository

import com.example.edward.newtube.api.YoutubeAPI
import java.util.concurrent.Executors


/**
 * Created by Edward on 6/22/2018.
 */
class YoutubeRepository private constructor() {

    private val networkExecutor = Executors.newFixedThreadPool(5)
    private val api by lazy { YoutubeAPI.createAPI() }

    fun getRepository() =
            InMemoryByPageKeyedRepository (youtubeApi = api, networkExecutor = this.networkExecutor)

    companion object {
        private val LOCK = Any()
        private var instance: YoutubeRepository? = null
        fun getInstance(): YoutubeRepository {
            instance
                    ?: synchronized(LOCK) {
                        instance
                                ?: YoutubeRepository().also { instance = it }
                    }
            return instance!!
        }
    }
}