package com.autolink.sayaradz.notification

import android.content.Context
import com.autolink.sayaradz.notification.manager.AnnouncementsNotificationManager
import com.autolink.sayaradz.notification.manager.OffersNotificationManager
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
                NotificationChannels.OFFERS_CHANNEL.key -> OffersNotificationManager(context)
                NotificationChannels.ANNOUNCEMENTS_CHANNEL.key -> AnnouncementsNotificationManager(context)
                else -> throw IllegalArgumentException("No such notification channel")
            }

        }
    }
}