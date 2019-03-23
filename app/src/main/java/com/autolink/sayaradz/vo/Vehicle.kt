package com.autolink.sayaradz.vo

import com.google.gson.annotations.SerializedName

data class Vehicle(

    @SerializedName("couleur")
    val colorCode:String,
    @SerializedName("modele")
    val modelCode:String,
    @SerializedName("version")
    val versionCode: String,
    @SerializedName("marque")
    val brandId:String,
    @SerializedName("num_chassi")
    val id:String
)