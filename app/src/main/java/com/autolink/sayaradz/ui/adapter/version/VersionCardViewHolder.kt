package com.autolink.sayaradz.ui.adapter.version

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.autolink.sayaradz.R
import com.autolink.sayaradz.ui.adapter.BaseViewHolder
import com.autolink.sayaradz.vo.Version
import com.bumptech.glide.RequestManager

class VersionCardViewHolder(view: View,
                            glide: RequestManager,
                            private val listener: VersionsAdapter.OnVersionClickListener): BaseViewHolder<Version>(view,glide) {

    val versionImage  = view.findViewById<ImageView>(R.id.item_image_view)

    init {

        view.setOnClickListener {
            val version  = it.tag as Version
            listener.onVersionClick(version,view.findViewById(R.id.item_image_view))
        }
    }


    companion object {
        fun create(parent: ViewGroup, glide: RequestManager, listener:VersionsAdapter.OnVersionClickListener): VersionCardViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_list_card_image, parent, false)
            return VersionCardViewHolder(view, glide,listener )
        }
    }


    override fun bindTo(o: Version) {
        with(o) {
            glide.load(photoURL)
                .into(versionImage)
            versionImage.transitionName = id
            view.tag = o
        }
    }
}
