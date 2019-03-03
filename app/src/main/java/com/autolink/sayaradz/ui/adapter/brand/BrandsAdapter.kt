package com.autolink.sayaradz.ui.adapter.brand

import android.util.Log
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.autolink.sayaradz.vo.Brand
import com.bumptech.glide.RequestManager
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView

class BrandsAdapter(private val glide: RequestManager): PagedListAdapter<Brand, BrandViewHolder>(BRAND_COMPARATOR),
    FastScrollRecyclerView.SectionedAdapter{




    override fun getSectionName(position: Int): String  = getItem(position)?.name?.substring(0,1).toString()

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



    override fun onBindViewHolder(brand: BrandViewHolder, position: Int) {
         brand.bindTo(getItem(position)!!)
    }




    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = BrandViewHolder.create(parent,glide)





}