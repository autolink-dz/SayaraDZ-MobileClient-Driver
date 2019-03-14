package com.autolink.sayaradz.ui.adapter

import android.util.Log
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import com.autolink.sayaradz.ui.adapter.brand.BrandViewHolder
import com.autolink.sayaradz.vo.Brand
import com.bumptech.glide.RequestManager
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView

abstract class BaseAdapter<T>(private val glide: RequestManager,diffUtil: DiffUtil.ItemCallback<T>):
                    PagedListAdapter<T, BaseViewHolder<T>>(diffUtil){



    override fun onBindViewHolder(viewHolder: BaseViewHolder<T>, position: Int) {
        viewHolder.bindTo(getItem(position)!!)
    }






}