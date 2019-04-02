package com.autolink.sayaradz.ui.adapter

import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import com.bumptech.glide.RequestManager

abstract class BaseAdapter<T>(private val glide: RequestManager,diffUtil: DiffUtil.ItemCallback<T>):
                    PagedListAdapter<T, BaseViewHolder<T>>(diffUtil){



    override fun onBindViewHolder(viewHolder: BaseViewHolder<T>, position: Int) {
        viewHolder.bindTo(getItem(position)!!)
    }






}