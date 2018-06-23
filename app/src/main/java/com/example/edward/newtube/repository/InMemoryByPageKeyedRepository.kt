package com.example.edward.newtube.repository

import android.arch.lifecycle.Transformations
import android.arch.paging.LivePagedListBuilder
import android.support.annotation.MainThread
import com.example.edward.newtube.api.YoutubeAPI
import com.example.edward.newtube.api.YoutubeDataSourceFactory
import com.example.edward.newtube.model.LiveDataPagedListing
import com.example.edward.newtube.model.QueryData
import com.example.edward.newtube.model.VideoModel
import java.util.concurrent.Executor


/**
 * Created by Edward on 6/22/2018.
 */
class InMemoryByPageKeyedRepository(private val youtubeApi: YoutubeAPI,
                                    private val networkExecutor: Executor) {

    @MainThread  // this function will be called in ViewModel for search videos
    fun postsOfSearchYoutube(searchYoutube: QueryData, pageSize: Int): LiveDataPagedListing<VideoModel> {

        val sourceFactory = YoutubeDataSourceFactory(youtubeApi, searchYoutube, networkExecutor)

        val livePagedList =
                LivePagedListBuilder(sourceFactory, pageSize)
                        .setFetchExecutor(networkExecutor)
                        .build()

        return LiveDataPagedListing(
                pagedList = livePagedList,
                networkState = Transformations.switchMap(sourceFactory.sourceLiveData) { it.networkState },
                refreshState = Transformations.switchMap(sourceFactory.sourceLiveData) { it.initialLoad },
                refresh = { sourceFactory.sourceLiveData.value?.retryAllFailed() },
                retry = { sourceFactory.sourceLiveData.value?.retryAllFailed() }
        )
    }
}