package com.autolink.sayaradz.vo

import com.google.gson.annotations.SerializedName
import java.util.*

data class Offer(
    val id:String,
    @SerializedName("id_annonce")
    val announcementId:String,
    @SerializedName("id_proprietaire")
    val ownerId:String,
    @SerializedName("id_client")
    val clientId:String,
    @SerializedName("prix")
    val price:Float,
    val data:Date
)