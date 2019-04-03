package com.autolink.sayaradz.vo

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CarDriver(
    val id:String,
    @SerializedName("nom")
    val  name:String,
    val  email:String,
    val  photoURL:String,
    @SerializedName("instance_token")
    val  instanceIdToken :String = "",
    @SerializedName("modeles")
    val  followedModels: MutableList<String> = mutableListOf(),
    @SerializedName("versions")
    val  followedVersions:MutableList<String> = mutableListOf()
): Parcelable