package com.example.edward.newtube

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.arch.paging.PagedList
import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SearchView
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.example.edward.newtube.adapter.MainVideoListAdapter
import com.example.edward.newtube.model.NetworkState
import com.example.edward.newtube.model.VideoModel
import com.example.edward.newtube.util.DEFAULT_QUERY
import com.example.edward.newtube.util.GlideApp
import com.example.edward.newtube.util.KEY_QUERY
import com.example.edward.newtube.viewmodel.VideoViewModel
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val queryViewModel by lazy {
        ViewModelProviders.of(this).get(VideoViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initAdapter()
        initSwipeToRefresh()
        initSearch()

        val query = savedInstanceState?.getString(KEY_QUERY) ?: DEFAULT_QUERY
        queryViewModel.showSearchQuery(query)
    }

    private fun initAdapter() {

        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE){
            mainListView.layoutManager = GridLayoutManager(this, 2)
        } else {
            mainListView.layoutManager = LinearLayoutManager(this)
        }
        mainListView.setHasFixedSize(true)

        val glide = GlideApp.with(this)
        val adapter = MainVideoListAdapter(
                glide,
                { queryViewModel.retry() },
                {
//                    val intent = Intent(this@MainActivity, VideoPlayActivity::class.java)
//                    ChannelModel(it.title, "", it.date, it.thumbnail, it.videoId)
//                    intent.putExtra(CHANNEL_MODEL,
//                            ChannelModel(it.title, "", it.date, it.thumbnail, it.videoId))
//                    startActivity(intent)
                    Toast.makeText(this, "you clicked this item.", Toast.LENGTH_SHORT).show()
                })

        mainListView.adapter = adapter

        queryViewModel.videoList.observe(this, Observer<PagedList<VideoModel>> {
            adapter.submitList(it)
        })
        queryViewModel.networkState.observe(this, Observer {
            adapter.setNetworkState(it)
        })
    }

    private fun initSwipeToRefresh() {
        queryViewModel.refreshState.observe(this, Observer {
            swipeRefreshLayout.isRefreshing = it == NetworkState.LOADING
        })
        swipeRefreshLayout.setOnRefreshListener {
            queryViewModel.refresh()
        }
    }

    private fun initSearch() {
        searchViewQuery.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null){
                    query.trim().let {
                        if (it.isNotEmpty()){
                            if (queryViewModel.showSearchQuery(it)){
                                mainListView.scrollToPosition(0)
                                (mainListView.adapter as? MainVideoListAdapter)?.submitList(null)
                            }
                        }
                    }
                }

                searchViewQuery.onActionViewCollapsed()
//                hideKeyboard()
                Log.d("SearchViewQUERY", "queryString: ${query?.trim()}")
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })
    }

    private fun hideKeyboard() {
        val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (inputManager.isAcceptingText){
            inputManager.hideSoftInputFromWindow(currentFocus.windowToken, 0)
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putString(KEY_QUERY, queryViewModel.currentQuery())
    }
}
