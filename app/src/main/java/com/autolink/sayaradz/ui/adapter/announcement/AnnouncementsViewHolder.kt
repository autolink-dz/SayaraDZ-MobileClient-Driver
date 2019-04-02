package com.autolink.sayaradz.ui.adapter.announcement

import android.annotation.SuppressLint
import android.content.Context
import android.icu.text.RelativeDateTimeFormatter
import android.os.Build
import android.text.format.DateUtils
import android.text.format.DateUtils.HOUR_IN_MILLIS
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.autolink.sayaradz.R
import com.autolink.sayaradz.ui.adapter.BaseViewHolder
import com.autolink.sayaradz.util.dp2px
import com.autolink.sayaradz.vo.Announcement
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import java.util.*


class AnnouncementsViewHolder(view: View,
                      glide: RequestManager,
                      val listener: AnnouncementsAdapter.OnAnnouncementClickListener): BaseViewHolder<Announcement>(view,glide) {

    private val mOwnerImageView = view.findViewById<ImageView>(R.id.owner_image_view)
    private val mAnnounceImageView = view.findViewById<ImageView>(R.id.announce_image_view)
    private val mVehicleNameTextView = view.findViewById<TextView>(R.id.vehicle_name_text_view)
    private val mVehiclePriceTextView = view.findViewById<TextView>(R.id.vehicle_price_text_view)
    private val mBrandImageView = view.findViewById<ImageView>(R.id.brand_image_view)
    private val mDistanceTextView  = view.findViewById<TextView>(R.id.vehicle_distance_text_view)
    private val mVehicleYearTextView  = view.findViewById<TextView>(R.id.vehicle_year_text_view)
    private val mAnnounceDate  = view.findViewById<TextView>(R.id.announce_date)


    init {
        view.setOnClickListener {
            val announcement  = it.tag as Announcement
            listener.onAnnouncementClick(announcement)
        }
    }


    companion object {
        fun create(parent: ViewGroup, glide: RequestManager, listener: AnnouncementsAdapter.OnAnnouncementClickListener): AnnouncementsViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_list_announcement, parent, false)
            return AnnouncementsViewHolder(view, glide,listener = listener)
        }
    }



    @SuppressLint("SetTextI18n")
    override fun bindTo(o: Announcement) {
        with(o){
            mVehiclePriceTextView.text = view.context.getString(R.string.currency_placeholder,price.toInt().toString())

            mAnnounceDate.text = DateUtils.getRelativeTimeSpanString(date.time, Date().time,HOUR_IN_MILLIS)
            mVehicleNameTextView.text = version.name
            mDistanceTextView.text = view.context.getString(R.string.distance_placeholder,distance.toInt().toString())+ " • "
            mVehicleYearTextView.text = year

            glide.load(owner.photoURL).apply(RequestOptions().centerCrop().transform(RoundedCorners(dp2px(view.context,20F)))).into(mOwnerImageView)
            glide.load(brand.photoURL).into(mBrandImageView)
            glide.load(photoURL).into(mAnnounceImageView)

            view.tag = o
        }
    }
}
