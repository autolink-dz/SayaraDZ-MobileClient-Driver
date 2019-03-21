package com.autolink.sayaradz.ui.adapter.version

import android.util.Log
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import com.autolink.sayaradz.ui.adapter.BaseAdapter
import com.autolink.sayaradz.ui.adapter.BaseViewHolder
import com.autolink.sayaradz.vo.Model
import com.autolink.sayaradz.vo.Version
import com.bumptech.glide.RequestManager

class VersionsAdapter(private val glide: RequestManager,
                      private val listener:OnVersionClickListener,
                      private val model:Model): BaseAdapter<Version>(glide, VERSION_COMPARATOR){

    companion object {
        private const val TAG  = "ModelsAdapter"

        val VERSION_COMPARATOR  = object : DiffUtil.ItemCallback<Version>(){
            override fun areItemsTheSame(oldItem: Version, newItem: Version): Boolean {
                return  oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Version, newItem: Version): Boolean {
                return  oldItem.name == newItem.name
            }
        }
    }

    interface OnVersionClickListener{
        fun onVersionClick(version: Version,imageView: ImageView)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VersionViewHolder.create(parent,glide,listener)


    override fun onBindViewHolder(viewHolder: BaseViewHolder<Version>, position: Int) {
        val version = getItem(position)!!
        version.modelCode = model.code
        version.nonSupportedOptions = (version.options + model.options).groupBy { it.code }
                                         .filter { it.value.size == 1 }
                                         .flatMap { it.value }

        viewHolder.bindTo(version)
    }



}