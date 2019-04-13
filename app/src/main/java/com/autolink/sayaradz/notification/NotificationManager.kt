package com.autolink.sayaradz.notification

import android.content.Context
import androidx.core.app.NotificationCompat
import org.json.JSONObject

abstract class NotificationManager(val context:Context){

    protected lateinit var mNotificationBuilder:NotificationCompat.Builder

    open fun setPayload(data: Map<String,String >):String{

        return data.getValue("id")
    }

    fun build() = mNotificationBuilder.build()
}
