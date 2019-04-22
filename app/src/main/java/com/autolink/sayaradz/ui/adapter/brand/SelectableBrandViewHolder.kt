package com.autolink.sayaradz.ui.adapter.brand

import android.animation.ObjectAnimator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.autolink.sayaradz.R
import com.autolink.sayaradz.ui.adapter.BaseViewHolder
import com.autolink.sayaradz.ui.adapter.brand.SelectableBrandsAdapter.Companion.NON_SELECTED_VIEW_TYPE
import com.autolink.sayaradz.ui.adapter.brand.SelectableBrandsAdapter.Companion.SELECTED_VIEW_TYPE
import com.autolink.sayaradz.vo.Brand
import com.bumptech.glide.RequestManager
import com.google.android.material.card.MaterialCardView

class SelectableBrandViewHolder(view: View,
                                val viewType:Int,
                                glide: RequestManager,
                                val listener:BrandsAdapter.OnBrandsClickListener): BaseViewHolder<Brand>(view,glide) {

    init {
        view.setOnClickListener {

            if( checkedIcon.visibility == View.VISIBLE){

                cardView.strokeColor  = it.context.resources.getColor(android.R.color.transparent)
                checkedIcon.visibility = View.GONE
                cardLaterLayout.visibility  = View.GONE

                brandImage.animate()
                    .scaleX(1F)
                    .scaleY(1F)
                    .setDuration(150)
                    .withEndAction {
                        listener.onBrandClick(it.tag as Brand)
                    }
                    .start()


            }else{

                cardView.strokeColor  = it.context.resources.getColor(R.color.colorPrimary)
                checkedIcon.visibility = View.VISIBLE
                cardLaterLayout.visibility  = View.VISIBLE

                brandImage.animate()
                    .scaleX(0.7F)
                    .scaleY(0.7F)
                    .setDuration(150)
                    .withEndAction {
                        listener.onBrandClick(it.tag as Brand)
                    }
                    .start()


            }




        }
    }

    companion object {
        fun create(parent: ViewGroup,viewType:Int, glide: RequestManager, listener: BrandsAdapter.OnBrandsClickListener): SelectableBrandViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_list_card_brand, parent, false)
            return SelectableBrandViewHolder(view,viewType, glide,listener)
        }
    }


    private val brandImage = view.findViewById<ImageView>(R.id.brand_image)
    private val cardView   = view.findViewById<MaterialCardView>(R.id.brand_card_view)
    private val checkedIcon  = view.findViewById<ImageView>(R.id.checked_brand_image_view)
    private val cardLaterLayout = view.findViewById<FrameLayout>(R.id.card_layer_layout)


    override fun bindTo(o: Brand) {


        when(viewType){

            NON_SELECTED_VIEW_TYPE ->{

                checkedIcon.visibility = View.GONE
                cardLaterLayout.visibility  = View.GONE
                cardView.strokeColor = view.context.resources.getColor(android.R.color.transparent)
                brandImage.apply {
                    scaleX = 1F
                    scaleY = 1F
                }

            }

            SELECTED_VIEW_TYPE -> {

                checkedIcon.visibility = View.VISIBLE
                cardLaterLayout.visibility  = View.VISIBLE
                cardView.strokeColor = view.context.resources.getColor(R.color.colorPrimary)
                brandImage.apply {
                    scaleX = 0.7F
                    scaleY = 0.7F
                }

            }
        }

        with(o) {
            glide.load(photoURL)
                .placeholder(R.drawable.ic_placeholder)
                .into(brandImage)
            view.tag = o
        }
    }

}