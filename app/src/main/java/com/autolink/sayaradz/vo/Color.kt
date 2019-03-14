package com.autolink.sayaradz.vo

import com.google.gson.annotations.SerializedName

data class Color(
    val code:String,
    @SerializedName("nom")
    val name:String
)