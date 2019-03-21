package com.autolink.sayaradz.vo

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Version(
    val id:String,
    @SerializedName("id_marque")
    val brandId:String,
    @SerializedName("id_modele")
    val modelId:String,
    @SerializedName("nom")
    val name:String,
    @SerializedName("url")
    val photoURL:String,
    val code:String,
    @SerializedName("fiche_tech")
    val specs:HashMap<String,String>,
    val options:List<Option>,
    var nonSupportedOptions:List<Option>,
    @SerializedName("couleurs")
    val colors:List<Color>,
    var modelCode:String): Parcelable