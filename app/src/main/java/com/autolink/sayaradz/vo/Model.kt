package com.autolink.sayaradz.vo

import com.google.gson.annotations.SerializedName

data class Model(
    val id:String,
    @SerializedName("nom")
    val name:String,
    @SerializedName("url")
    val photoURL:String,
    @SerializedName("id_marque")
    val brandId:String,
    val options:List<Option>,
    @SerializedName("couleurs")
    val colors:List<Color>
)