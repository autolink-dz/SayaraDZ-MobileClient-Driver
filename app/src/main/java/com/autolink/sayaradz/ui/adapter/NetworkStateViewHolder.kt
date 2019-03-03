package com.autolink.sayaradz.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.recyclerview.widget.RecyclerView
import com.autolink.sayaradz.R
import com.autolink.sayaradz.repository.utils.NetworkState
import com.autolink.sayaradz.repository.utils.Status

class NetworkStateViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    private val progressBar = view.findViewById<ProgressBar>(R.id.progress_bar)

    fun bindTo(networkState: NetworkState?) {
        progressBar.visibility = toVisibility(networkState?.status == Status.RUNNING)

    }

    companion object {
        fun create(parent: ViewGroup): NetworkStateViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_network_state, parent, false)
            return NetworkStateViewHolder(view)
        }

        fun toVisibility(constraint : Boolean): Int {
            return if (constraint) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }
    }
}