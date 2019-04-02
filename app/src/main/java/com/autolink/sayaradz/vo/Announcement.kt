package com.autolink.sayaradz.vo

import com.google.gson.annotations.SerializedName
import java.util.*

data class Announcement(
    val id:String,
    val photoURL:String,
    val price:Float,
    val year:String,
    val date:Date,
    val distance:Float,
    val owner:CarDriver,
    val brand:Brand,
    val model:Model,
    val version:Version)


data class CompactAnnouncement(
    val id:String,
    @SerializedName("id_proprietaire")
    val ownerId:String,
    @SerializedName("id_marque")
    val brandId:String,
    @SerializedName("id_modele")
    val modelId:String,
    @SerializedName("id_version")
    val versionId:String,
    @SerializedName("url")
    val photoURL:String,
    @SerializedName("prix_min")
    val price:Float,
    @SerializedName("annee")
    val year:String,
    val date:Date,
    val distance:Float
)