package com.example.edward.newtube.api

import android.arch.lifecycle.MutableLiveData
import android.arch.paging.DataSource
import com.example.edward.newtube.model.QueryData
import com.example.edward.newtube.model.VideoModel
import java.util.concurrent.Executor


/**
 * Created by Edward on 6/22/2018.
 */
class YoutubeDataSourceFactory(
        private val youtubeApi: YoutubeAPI,
        private val searchQuery: QueryData,
        private val retryExecutor: Executor) : DataSource.Factory<String, VideoModel>() {

    val sourceLiveData = MutableLiveData<PageKeyedYoutubeDataSource>()

    override fun create(): DataSource<String, VideoModel> {
        val source = PageKeyedYoutubeDataSource(youtubeApi, searchQuery, retryExecutor)
        sourceLiveData.postValue(source)
        return source
    }

}