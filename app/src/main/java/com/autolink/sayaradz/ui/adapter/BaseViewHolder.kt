package com.autolink.sayaradz.ui.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager


abstract class BaseViewHolder<T>(val view: View, val glide: RequestManager): RecyclerView.ViewHolder(view) {

    abstract fun bindTo(o: T)
}