package com.autolink.sayaradz.util

import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.forEach
import androidx.viewpager.widget.ViewPager
import kotlinx.android.synthetic.main.fragment_intro_slide_1.view.*


class CostumePageTransformer: ViewPager.PageTransformer {

    companion object {
        private val TAG = "CostumePageTransformer"
    }

    override fun transformPage(view: View, position: Float) {

        (view as ViewGroup).forEachViewOfType<TextView>{ textView, index ->
            textView.translationX =  position *75f*(index%100)
            textView.alpha = 1.0F - 1.5F *Math.abs(position)
        }




    }
}