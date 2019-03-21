package com.autolink.sayaradz.ui.adapter.version

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.ViewCompat
import com.autolink.sayaradz.R
import com.autolink.sayaradz.ui.adapter.BaseViewHolder
import com.autolink.sayaradz.vo.Version
import com.bumptech.glide.RequestManager

class VersionViewHolder(view: View,
                        glide: RequestManager,
                        private val listener: VersionsAdapter.OnVersionClickListener): BaseViewHolder<Version>(view,glide) {

    val versionImage  = view.findViewById<ImageView>(R.id.version_image)
    val versionName  = view.findViewById<TextView>(R.id.version_name)

    init {

        view.setOnClickListener {
            val version  = it.tag as Version
            listener.onVersionClick(version,view.findViewById(R.id.version_image))
        }
    }


    companion object {
        fun create(parent: ViewGroup, glide: RequestManager,listener:VersionsAdapter.OnVersionClickListener): VersionViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_list_version, parent, false)
            return VersionViewHolder(view, glide,listener )
        }
    }


    override fun bindTo(o: Version) {
        with(o) {
            glide.load(photoURL)
                .into(versionImage)
            versionName.text = name
            versionImage.transitionName = id
            view.tag = o
        }
    }
}
