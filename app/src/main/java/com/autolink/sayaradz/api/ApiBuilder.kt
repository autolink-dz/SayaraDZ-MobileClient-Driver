package com.autolink.sayaradz.api

import android.util.Log
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

open class ApiBuilder{
companion object {
    fun <T> create(baseUrl:String,service:Class<T>): T {
        val httpUrl = HttpUrl.parse(baseUrl)

        val client =  OkHttpClient
            .Builder()
            .build()

        return Retrofit.Builder()
            .baseUrl(httpUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
            .create(service)
    }
}


}