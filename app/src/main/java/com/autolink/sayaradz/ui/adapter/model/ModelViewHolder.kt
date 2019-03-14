package com.autolink.sayaradz.ui.adapter.model

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.autolink.sayaradz.R
import com.autolink.sayaradz.ui.adapter.BaseViewHolder
import com.autolink.sayaradz.vo.Model
import com.bumptech.glide.RequestManager


class ModelViewHolder(view: View,glide: RequestManager): BaseViewHolder<Model>(view,glide) {

    companion object {
        fun create(parent: ViewGroup, glide: RequestManager): ModelViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_list_model, parent, false)
            return ModelViewHolder(view, glide)
        }
    }


    val modelImage: ImageView = view.findViewById(R.id.model_image)
    val modelName: TextView = view.findViewById(R.id.model_name)
    val modelOptionsNumber: TextView = view.findViewById(R.id.model_options_number)
    val modelColorsNumber: TextView = view.findViewById(R.id.model_colors_number)



    override fun bindTo(o: Model) {
        with(o) {
            modelName.text = name
            glide.load(photoURL)
                .into(modelImage)
        }
    }
}
