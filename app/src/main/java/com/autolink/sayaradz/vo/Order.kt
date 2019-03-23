package com.autolink.sayaradz.vo

import com.google.gson.annotations.SerializedName

data class Order(
    @SerializedName("id_automobiliste")
    val uid:String,
    @SerializedName("id_marque")
    val brandId: String,
    @SerializedName("id_vehicule")
    val vehicleId:String,
    @SerializedName("prix")
    val vehiclePrice:Float,
    @SerializedName("versement")
    val payment:Float=0F
)