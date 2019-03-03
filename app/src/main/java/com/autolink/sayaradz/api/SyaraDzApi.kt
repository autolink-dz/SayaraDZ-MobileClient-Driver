package com.autolink.sayaradz.api

import com.autolink.sayaradz.vo.Brand
import com.google.gson.annotations.SerializedName
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.*


interface SayaraDzApi{

    @GET("marques")
    fun GetBrandsList(@Query("next")key:String="0"):Observable<ResponseListing<Brand>>


    data class ResponseListing<T>(
            @SerializedName("next")
            val key:String,
            val data:List<T>)


}