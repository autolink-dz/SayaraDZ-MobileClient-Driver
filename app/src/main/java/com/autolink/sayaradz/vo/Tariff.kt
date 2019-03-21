package com.autolink.sayaradz.vo

import com.google.gson.annotations.SerializedName

data class Tariff(
    @SerializedName("marque")
    val BrandId:String,
    @SerializedName("modele")
    val modelId:String,
    val type:TariffType,
    val code:String,
    @SerializedName("prix")
    val price:Float
)


enum class TariffType(type: Int) {
    Version(0),
    Color(1),
    Option(2)
}