package com.autolink.sayaradz.api

import com.autolink.sayaradz.vo.*
import com.google.gson.annotations.SerializedName
import io.reactivex.Observable
import retrofit2.Call
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
    fun getVehicle(@Path("id_marque")brandId:String,
                   @Query("modele") modelCode:String,
                   @Query("version") versionCode:String,
                   @Query("couleur") colorCode:String,
                   @Query("disponible") available:Boolean = true,
                   @Query("options") optionsList:String? = null):Observable<ResponseListing<Vehicle>>

    @FormUrlEncoded
    @POST("commandes/")
    fun setOrder(@Field("id_automobiliste")uid:String,
                 @Field("id_marque")brandId: String,
                 @Field("id_version")versionId: String,
                 @Field("id_vehicule") vehicleId:String,
                 @Field("prix") vehiclePrice:Float,
                 @Field("versement")payment:Float=0F):Observable<Order>


    @FormUrlEncoded
    @POST("notifications/tokens/{id}")
    fun setUserInstanceIdToken(@Path("id")id:String,@Field("instance_token")token:String):Call<Any>

    @FormUrlEncoded
    @POST("notifications/{type}/{id}")
    fun follow(@Path("type") type: String,@Path("id") id: String,@Field("id_automobiliste")uid: String):Observable<Any>

    @FormUrlEncoded
    @HTTP(method = "DELETE", path = "notifications/{type}/{id}", hasBody = true)
    fun unfollow(@Path("type") type: String,@Path("id") id: String,@Field("id_automobiliste")uid: String):Observable<Any>

    @GET("automobilistes/{uid}")
    fun getCarDriver(@Path("uid") uid:String):Observable<CarDriver>

    @GET("annonces")
    fun getAnnouncements(@Query("next")key:String="0",@Query("disponible") available:Boolean = true):Observable<ResponseListing<CompactAnnouncement>>

    @FormUrlEncoded
    @POST("offres")
    fun setOffer(@Field("id_annonce") announcementId:String,
                 @Field("id_proprietaire")ownerID:String,
                 @Field("id_client")clientId:String,
                 @Field("prix")price:Float):Observable<Order>


    @FormUrlEncoded
    @PUT("offres/{id}")
    fun setOfferState(@Path("id") offerId:String,
                      @Field("etat")state:String):Call<Any>

    data class ResponseListing<T>(
            @SerializedName("next")
            val key:String,
            val data:List<T>,
            val extras: Extras = Extras()
    )

    data class Extras(
        @SerializedName("automobilistes")
        val carDrivers:Map<String,CarDriver> = mapOf(),
        @SerializedName("marques")
        val brands:Map<String,Brand>  = mapOf(),
        @SerializedName("modeles")
        val models:Map<String,Model>  = mapOf(),
        val versions:Map<String,Version> = mapOf()
    )
}