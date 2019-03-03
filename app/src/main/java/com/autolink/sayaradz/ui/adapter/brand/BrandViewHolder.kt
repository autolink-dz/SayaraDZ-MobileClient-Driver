package com.autolink.sayaradz.ui.adapter.brand

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.autolink.sayaradz.R
import com.autolink.sayaradz.vo.Brand
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager


class BrandViewHolder(val view:View, private val glide:RequestManager): RecyclerView.ViewHolder(view) {

    companion object {
        fun create(parent: ViewGroup, glide: RequestManager): BrandViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.list_item_brand, parent, false)
            return BrandViewHolder(view, glide)
        }
    }


    val brandImage: ImageView = view.findViewById(R.id.brand_image)
    val brandTitle: TextView = view.findViewById(R.id.brand_name)


    fun bindTo(brand: Brand) {
        with(brand) {
            brandTitle.text = name
            glide.load(photoURL)
                .into(brandImage)
        }
    }

}