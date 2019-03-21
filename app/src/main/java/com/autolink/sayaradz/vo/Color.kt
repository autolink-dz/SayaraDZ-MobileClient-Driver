package com.autolink.sayaradz.vo

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Color(
    val code:String,
    @SerializedName("nom")
    val name:String,
    val hex:String
): Parcelable