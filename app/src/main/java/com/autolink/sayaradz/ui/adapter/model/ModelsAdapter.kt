package com.autolink.sayaradz.ui.adapter.model

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.autolink.sayaradz.ui.adapter.BaseAdapter
import com.autolink.sayaradz.ui.adapter.brand.BrandViewHolder
import com.autolink.sayaradz.vo.Brand
import com.autolink.sayaradz.vo.Model
import com.bumptech.glide.RequestManager
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView

class ModelsAdapter(private val glide: RequestManager): BaseAdapter<Model>(glide,MODEL_COMPARATOR){

    companion object {
        private const val TAG  = "ModelsAdapter"

        val MODEL_COMPARATOR  = object : DiffUtil.ItemCallback<Model>(){
            override fun areItemsTheSame(oldItem: Model, newItem: Model): Boolean {
                return  oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Model, newItem: Model): Boolean {
                return  oldItem.name == newItem.name
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ModelViewHolder.create(parent,glide)





}