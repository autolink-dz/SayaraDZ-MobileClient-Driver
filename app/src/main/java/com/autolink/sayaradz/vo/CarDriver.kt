package com.autolink.sayaradz.vo

import com.google.gson.annotations.SerializedName

data class CarDriver(
    val id:String,
    @SerializedName("nom")
    val  name:String,
    val  email:String,
    val  photoURL:String,
    val  token :String?=""
)