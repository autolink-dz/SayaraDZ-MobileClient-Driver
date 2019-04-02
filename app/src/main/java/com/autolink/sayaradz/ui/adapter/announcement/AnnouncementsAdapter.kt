package com.autolink.sayaradz.ui.adapter.announcement

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.autolink.sayaradz.ui.adapter.BaseAdapter
import com.autolink.sayaradz.ui.adapter.BaseViewHolder
import com.autolink.sayaradz.ui.adapter.brand.BrandViewHolder
import com.autolink.sayaradz.vo.Announcement
import com.autolink.sayaradz.vo.Brand
import com.bumptech.glide.RequestManager
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView

class AnnouncementsAdapter( private val glide: RequestManager,
                            private val listener:OnAnnouncementClickListener): BaseAdapter<Announcement>(glide,ANNOUNCEMENT_COMPARATOR){



    companion object {
        private const val TAG  = "AnnouncementsAdapter"

        val ANNOUNCEMENT_COMPARATOR  = object : DiffUtil.ItemCallback<Announcement>(){
            override fun areItemsTheSame(oldItem: Announcement, newItem: Announcement): Boolean {
                return  oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Announcement, newItem: Announcement): Boolean {
                return  oldItem.id == newItem.id
            }
        }
    }

    interface OnAnnouncementClickListener{
        fun onAnnouncementClick(announcement: Announcement)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = AnnouncementsViewHolder.create(parent,glide,listener)





}