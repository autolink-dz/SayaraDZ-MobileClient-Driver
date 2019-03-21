package com.autolink.sayaradz.api

import com.autolink.sayaradz.vo.*
import com.google.gson.annotations.SerializedName
import io.reactivex.Observable
import retrofit2.http.*


interface SayaraDzApi{


    @GET("marques")
    fun getBrandsList(@Query("next")key:String="0"):Observable<ResponseListing<Brand>>

    @GET("modeles")
    fun getModelsList(@Query("next")key:String="0",
                      @Query("id_marque")brandId:String?=null):Observable<ResponseListing<Model>>

    @GET("versions")
    fun getVersionsList(@Query("next")key:String="0",
                        @Query("id_modele")modelId:String?=null):Observable<ResponseListing<Version>>

    @GET("tarifs/{id_marque}/{code_modele}/{type}/{code}")
    fun getItemPrice(@Path("id_marque")brandId:String,
                        @Path("code_modele")modelCode:String,
                        @Path("type")type:String,
                        @Path("code")code:String):Observable<Tariff>

    @GET("vehicules/{id_marque}")
    fun getVehicule(@Path("id_marque")brandId:String,
                    @Query("modele") modelCode:String,
                    @Query("version") versionCode:String,
                    @Query("couleur") colorCode:String,
                    @Query("options") optionsList:String? = null):Observable<ResponseListing<Vehicle>>



    data class ResponseListing<T>(
            @SerializedName("next")
            val key:String,
            val data:List<T>)
}