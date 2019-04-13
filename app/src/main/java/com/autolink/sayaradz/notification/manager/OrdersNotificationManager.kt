package com.autolink.sayaradz.notification.manager

import android.app.Notification
import android.content.Context
import android.text.Html
import android.util.Log
import androidx.core.app.NotificationCompat
import com.autolink.sayaradz.R
import com.autolink.sayaradz.notification.NotificationChannels
import com.autolink.sayaradz.notification.NotificationManager
import com.autolink.sayaradz.util.GlideApp
import com.bumptech.glide.Glide
import org.json.JSONObject

class OrdersNotificationManager(context:Context):NotificationManager(context){

    companion object {
        private const val TAG = "OrdersNotification"
    }


    override fun setPayload(data: Map<String,String>):String {

        mNotificationBuilder = NotificationCompat.Builder(context,NotificationChannels.ORDERS_CHANNEL.key)

        var title = ""
        var body = ""
        var expendableText = ""

        Log.d(TAG,"setPayload $data " )

        if(data.getValue("validation").toBoolean()){

               title  = context.getString(R.string.notification_order_accepted_title)
               body   = context.getString(R.string.notification_order_accepted_body,data.getValue("version"))

        } else{

               title  = context.getString(R.string.notification_order_rejected_title)
               body   = context.getString(R.string.notification_order_rejected_body,data.getValue("version"))
               expendableText  = data.getValue("message")

        }

        val futureTarget = GlideApp.with(context)
                                    .asBitmap()
                                    .load(data.getValue("photo"))
                                    .submit()


        val photo  = futureTarget.get()


        GlideApp.with(context).clear(futureTarget)

        mNotificationBuilder.setSmallIcon(R.drawable.ic_notification_logo)
                            .setColor(context.resources.getColor(R.color.colorPrimary))
                            .setContentTitle(title)
                            .setLargeIcon(photo)
                            .setStyle(NotificationCompat.BigTextStyle()
                                .bigText(Html.fromHtml("<strong>$body</strong><br/>$expendableText"))
                            )

        return super.setPayload(data)
    }
}
