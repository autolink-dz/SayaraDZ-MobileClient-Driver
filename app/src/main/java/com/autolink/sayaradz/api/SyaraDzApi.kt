package com.autolink.sayaradz.api

import com.autolink.sayaradz.vo.Brand
import com.autolink.sayaradz.vo.Model
import com.google.gson.annotations.SerializedName
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Query
import java.util.*


interface SayaraDzApi{


    @GET("marques")
    fun getBrandsList(@Query("next")key:String="0"):Observable<ResponseListing<Brand>>

    @GET("modeles")
    fun getModelsList(@Query("next")key:String="0",
                      @Query("id_marque")brandId:String?=null):Observable<ResponseListing<Model>>


    data class ResponseListing<T>(
            @SerializedName("next")
            val key:String,
            val data:List<T>)


}