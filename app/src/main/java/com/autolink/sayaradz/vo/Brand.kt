package com.autolink.sayaradz.vo

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Brand(
    val id:String,
    @SerializedName("nom")
    val name:String,
    @SerializedName("url")
    val photoURL:String) : Parcelable
