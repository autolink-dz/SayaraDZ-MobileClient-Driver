package com.autolink.sayaradz.api

import android.content.Context
import android.util.Log
import com.autolink.sayaradz.util.readFromSharedPreference
import com.google.firebase.auth.FirebaseAuth
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

open class ApiBuilder{
companion object {
    fun <T> create(baseUrl:String,service:Class<T>,context: Context): T {
        val httpUrl = HttpUrl.parse(baseUrl)

        val client =  OkHttpClient
            .Builder()
            .addInterceptor {
                val token  = context.readFromSharedPreference("TOKEN")

                val request  = it.request().newBuilder()
                    .addHeader("Authorization", "Bearer $token")
                    .build()
                it.proceed(request)
            }
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