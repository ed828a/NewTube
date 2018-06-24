package com.example.edward.newtube.adapter

import android.arch.paging.PagedListAdapter
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.load.resource.bitmap.TransformationUtils.centerInside
import com.example.edward.newtube.R
import com.example.edward.newtube.model.NetworkState
import com.example.edward.newtube.model.VideoModel
import com.example.edward.newtube.util.GlideRequests
import kotlinx.android.synthetic.main.cell_video.view.*


/**
 * Created by Edward on 6/22/2018.
 */
class MainVideoListAdapter(
        private val glide: GlideRequests,
        private val retryCallback: () -> Unit,
        val listener: (VideoModel) -> Unit) : PagedListAdapter<VideoModel, RecyclerView.ViewHolder>(COMPARATOR) {

    private var networkState: NetworkState? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            R.layout.cell_video -> {
                val view = LayoutInflater.from(parent.context)
                        .inflate(R.layout.cell_video, parent, false)
                MainVideoListViewHolder(view)
            }
            R.layout.cell_network_state -> {
                val view = LayoutInflater.from(parent.context)
                        .inflate(R.layout.cell_network_state, parent, false)

                NetworkStateViewHolder(view, retryCallback)
            }
            else -> throw IllegalArgumentException("unknown view type $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            R.layout.cell_video -> {
                (holder as MainVideoListViewHolder).bind(getItem(position)!!)
                holder.setOnItemSelectedListener(getItem(position)!!)
            }
            R.layout.cell_network_state -> (holder as NetworkStateViewHolder).bindTo(networkState)
        }
    }

    private fun hasExtraRow() = networkState != null && networkState != NetworkState.LOADED

    override fun getItemViewType(position: Int): Int {
        return if (hasExtraRow() && position == itemCount - 1) {
            R.layout.cell_network_state
        } else {
            R.layout.cell_video
        }
    }

    override fun getItemCount(): Int {
        return super.getItemCount() + if (hasExtraRow()) 1 else 0
    }


    // this function will be called in main activity
    fun setNetworkState(newNetworkState: NetworkState?) {
        val previousState = this.networkState
        val hadExtraRow = hasExtraRow()
        this.networkState = newNetworkState
        val hasExtraRow = hasExtraRow()
        if (hadExtraRow != hasExtraRow) {
            if (hadExtraRow) {
                notifyItemRemoved(super.getItemCount())
            } else {
                notifyItemInserted(super.getItemCount())
            }
        } else if (hasExtraRow && previousState != newNetworkState) {
            notifyItemChanged(itemCount - 1)
        }
    }

    inner class MainVideoListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textViewTitle = itemView.textViewTitle
        private val textViewDesc = itemView.textViewChannelTitle
        private val textViewDate = itemView.textViewDate
        private val imageViewThumb = itemView.imageViewThumb

        fun setOnItemSelectedListener(videoModel: VideoModel) {
            itemView.setOnClickListener {
                listener(videoModel)
            }
        }

        fun bind(videoModel: VideoModel) {
            textViewTitle.text = videoModel.title
            textViewDate.text = videoModel.date
            glide.load(videoModel.thumbnail).into(imageViewThumb)
        }
    }

    companion object {
        val COMPARATOR = object : DiffUtil.ItemCallback<VideoModel>() {
            override fun areItemsTheSame(oldItem: VideoModel?, newItem: VideoModel?): Boolean {
                return oldItem?.videoId == newItem?.videoId
            }

            override fun areContentsTheSame(oldItem: VideoModel?, newItem: VideoModel?): Boolean {
                return oldItem?.title == newItem?.title
            }
        }
    }
}