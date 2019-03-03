package com.autolink.sayaradz.vo

import com.google.gson.annotations.SerializedName

data class Brand(
    val id:String,
    @SerializedName("nom")
    val name:String,
    @SerializedName("url")
    val photoURL:String)
