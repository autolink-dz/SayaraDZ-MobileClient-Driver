package com.autolink.sayaradz.vo

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue

@Parcelize
data class Model(
    val id:String,
    @SerializedName("id_marque")
    val brandId:String,
    @SerializedName("nom")
    val name:String,
    @SerializedName("url")
    val photoURL:String,
    val code:String,
    val options:List<Option>,
    @SerializedName("couleurs")
    val colors:List<Color>
): Parcelable