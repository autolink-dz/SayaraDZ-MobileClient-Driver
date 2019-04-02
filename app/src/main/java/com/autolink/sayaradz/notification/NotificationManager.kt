package com.autolink.sayaradz.notification

import android.content.Context
import androidx.core.app.NotificationCompat
import org.json.JSONObject

abstract class NotificationManager(val context:Context){

    protected lateinit var mNotificationBuilder:NotificationCompat.Builder

    abstract fun setPayload(data: Map<String,String >)

    fun build() = mNotificationBuilder.build()
}
