package com.autolink.sayaradz.notification

import android.content.Context
import com.autolink.sayaradz.notification.manager.OrdersNotificationManager

enum class NotificationChannels(val key: String) {

    ORDERS_CHANNEL("0"),
    ANNOUNCEMENTS_CHANNEL("1"),
    OFFERS_CHANNEL("2")

}

class NotificationMangerFactory{



    companion object {

        fun create(context: Context, key:String):NotificationManager{
            return when(key){
                NotificationChannels.ORDERS_CHANNEL.key -> OrdersNotificationManager(context)
                else -> throw IllegalArgumentException("No such notification channel")
            }

        }
    }
}