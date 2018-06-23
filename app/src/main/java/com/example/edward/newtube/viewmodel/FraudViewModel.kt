package com.example.edward.newtube.viewmodel

import android.arch.lifecycle.MediatorLiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import com.example.edward.newtube.model.QueryData
import com.example.edward.newtube.model.Type
import com.example.edward.newtube.repository.YoutubeRepository
import com.example.edward.newtube.util.PAGEDLIST_PAGE_SIZE


/**
 * Created by Edward on 6/23/2018.
 */
class FraudViewModel {
    private val repository: YoutubeRepository = YoutubeRepository.getInstance()

    private val relatedVideoId: MutableLiveData<String> = MutableLiveData()
    private val queryString = MutableLiveData<String>()
    private var queryData = MediatorLiveData<QueryData>()
    init {
        queryData.addSource(relatedVideoId) { related ->
            queryData.value = QueryData(related ?: "", Type.RELATED_VIDEO_ID)
        }

        queryData.addSource(queryString) {
            queryData.value = QueryData(it ?: "", Type.QUERY_STRING)
        }
    }

    private val searchRelatedVideoResult =
            Transformations.map(queryData) {
                repository.getRepository().postsOfSearchYoutube(it, PAGEDLIST_PAGE_SIZE)
            }

    val videoList = Transformations.switchMap(searchRelatedVideoResult) { it.pagedList }!!
    val networkState = Transformations.switchMap(searchRelatedVideoResult) { it.networkState }!!
    val refreshState = Transformations.switchMap(searchRelatedVideoResult) { it.refreshState }!!

    fun refresh() {
        searchRelatedVideoResult.value?.refresh?.invoke()
    }

    fun showRelatedVideoIdQuery(relatedVideoId: String): Boolean =
            if (this.relatedVideoId.value == relatedVideoId) false
            else {
                this.relatedVideoId.value = relatedVideoId
                true
            }

    fun showSearchQuery(searchQuery: String): Boolean =
            if (queryString.value == searchQuery) false
            else {
                queryString.value = searchQuery
                true
            }

    fun retry(){
        val listing = searchRelatedVideoResult?.value
        listing?.retry?.invoke()
    }

    fun currentVideoId(): String? = this.relatedVideoId.value
}