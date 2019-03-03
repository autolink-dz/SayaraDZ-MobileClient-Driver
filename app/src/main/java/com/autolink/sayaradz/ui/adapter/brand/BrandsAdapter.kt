package com.autolink.sayaradz.ui.adapter.brand

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.autolink.sayaradz.R
import com.autolink.sayaradz.repository.utils.NetworkState
import com.autolink.sayaradz.ui.adapter.NetworkStateViewHolder
import com.autolink.sayaradz.vo.Brand
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView

class BrandsAdapter(private val glide: RequestManager): PagedListAdapter<Brand, RecyclerView.ViewHolder>(BRAND_COMPARATOR),
    FastScrollRecyclerView.SectionedAdapter{


    override fun getSectionName(position: Int): String  = getItem(position)?.name?.get(0)?.toString().toString()

    companion object {
        private const val TAG  = "BrandsFragment"

        val BRAND_COMPARATOR  = object : DiffUtil.ItemCallback<Brand>(){
            override fun areItemsTheSame(oldItem: Brand, newItem: Brand): Boolean {
                return  oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Brand, newItem: Brand): Boolean {
                return  oldItem.name == newItem.name
            }
        }
    }

    private var networkState: NetworkState? = null




    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        when(getItemViewType(position)){
            R.layout.list_item_brand -> (holder as BrandViewHolder).bindTo(getItem(position)!!)
            R.layout.list_item_network_state -> (holder as NetworkStateViewHolder).bindTo(networkState)
        }



    }

    override fun getItemViewType(position: Int): Int {
        return if (hasExtraRow() && position == itemCount - 1) {
            R.layout.list_item_network_state
        } else {
            R.layout.list_item_brand
        }
    }

    override fun getItemCount(): Int {
        return super.getItemCount() + if (hasExtraRow()) 1 else 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
       return when(viewType){
           R.layout.list_item_brand -> BrandViewHolder.create(parent,glide)
           R.layout.list_item_network_state -> NetworkStateViewHolder.create(parent)
           else -> throw IllegalArgumentException("unknown view type $viewType")
       }
    }





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

    private fun hasExtraRow() = networkState != null && networkState != NetworkState.LOADED

}