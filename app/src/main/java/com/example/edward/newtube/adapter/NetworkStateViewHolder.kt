package com.example.edward.newtube.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.edward.newtube.R
import com.example.edward.newtube.model.NetworkState
import com.example.edward.newtube.model.Status
import kotlinx.android.synthetic.main.cell_network_state.view.*


/**
 * Created by Edward on 6/22/2018.
 */
class NetworkStateViewHolder(itemView: View, private val retryCallback: () -> Unit) : RecyclerView.ViewHolder(itemView) {
    private val progressBar = itemView.progress_bar
    private val buttonRetry = itemView.retry_button
    private val textError = itemView.error_msg

    init {
        buttonRetry.setOnClickListener {
            retryCallback()
        }
    }

    fun bindTo(networkState: NetworkState?) {
        progressBar.visibility = toVisibility(networkState?.status == Status.RUNNING)
        buttonRetry.visibility = toVisibility(networkState?.status == Status.FAILED)
        textError.visibility = toVisibility(networkState?.msg != null)
        textError.text = networkState?.msg
    }

    //    fun toVisibility(constraint: Boolean) = if (constraint) View.VISIBLE else View.GONE
    fun toVisibility(constraint: Boolean) = View.GONE
}