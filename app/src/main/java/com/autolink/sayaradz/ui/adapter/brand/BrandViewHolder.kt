package com.autolink.sayaradz.ui.adapter.brand

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.autolink.sayaradz.R
import com.autolink.sayaradz.ui.adapter.BaseViewHolder
import com.autolink.sayaradz.vo.Brand
import com.bumptech.glide.RequestManager


class BrandViewHolder(view:View,
                      glide:RequestManager,
                      val listener:BrandsAdapter.OnBrandsClickListener): BaseViewHolder<Brand>(view,glide) {

    init {
        view.setOnClickListener {
            val brand  = it.tag as Brand
            listener.onBrandClick(brand)
        }
    }

    companion object {
        fun create(parent: ViewGroup, glide: RequestManager, listener: BrandsAdapter.OnBrandsClickListener): BrandViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_list_brand, parent, false)
            return BrandViewHolder(view, glide,listener)
        }
    }


    val brandImage: ImageView = view.findViewById(R.id.brand_image)
    val brandTitle: TextView = view.findViewById(R.id.brand_name)


    override fun bindTo(o: Brand) {
        with(o) {
            brandTitle.text = name
            glide.load(photoURL)
                .placeholder(R.drawable.ic_placeholder)
                .into(brandImage)
            view.tag = o
        }
    }

}