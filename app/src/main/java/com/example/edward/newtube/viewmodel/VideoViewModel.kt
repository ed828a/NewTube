package com.example.edward.newtube.viewmodel

import android.arch.lifecycle.*
import com.example.edward.newtube.model.QueryData
import com.example.edward.newtube.repository.YoutubeRepository


/**
 * Created by Edward on 6/22/2018.
 */
class VideoViewModel: ViewModel() {
    private val repository: YoutubeRepository = YoutubeRepository.getInstance()
    private val queryString = MutableLiveData<String>()
    private val queryData = Transformations.map(queryString){ QueryData(it) }
    private val searchResult =
            Transformations.map(queryData) {
                repository.getRepository().postsOfSearchYoutube(it, 30)
            }
    val videoList = Transformations.switchMap(searchResult, { it.pagedList })!!
    val networkState = Transformations.switchMap(searchResult, { it.networkState })!!
    val refreshState = Transformations.switchMap(searchResult, { it.refreshState })!!

    fun refresh() {
        searchResult.value?.refresh?.invoke()
    }

    fun showSearchQuery(searchQuery: String): Boolean =
            if (queryString.value == searchQuery) false
            else {
                queryString.value = searchQuery
                true
            }

    fun retry(){
        val listing = searchResult?.value
        listing?.retry?.invoke()
    }

    fun currentQuery(): String? = queryString.value

}