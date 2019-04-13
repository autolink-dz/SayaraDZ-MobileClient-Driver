package com.autolink.sayaradz.notification.manager

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.text.Html
import androidx.core.app.NotificationCompat
import com.autolink.sayaradz.R
import com.autolink.sayaradz.notification.NotificationChannels
import com.autolink.sayaradz.notification.NotificationManager
import com.autolink.sayaradz.service.OfferReplyNotificationService
import com.autolink.sayaradz.util.GlideApp
import com.bumptech.glide.request.RequestOptions

class AnnouncementsNotificationManager(context: Context): NotificationManager(context) {

    companion object {
        private const val TAG = "AnnouncementsNotification"
    }


    override fun setPayload(data: Map<String, String>):String{

        mNotificationBuilder = NotificationCompat.Builder(context, NotificationChannels.ANNOUNCEMENTS_CHANNEL.key)

        var title = ""
        var body = ""

        if(data.getValue("validation").toBoolean()){

            title  = context.getString(R.string.notification_offer_accepted_title)
            body   = context.getString(R.string.notification_offer_accepted_body,data.getValue("owner"),data.getValue("version"),data.getValue("contact"))

        } else{
            title  = context.getString(R.string.notification_offer_rejected_title)
            body   = context.getString(R.string.notification_offer_rejected_body,data.getValue("owner"),data.getValue("version"))

        }



        val futureTarget = GlideApp.with(context)
            .asBitmap()
            .load(data.getValue("photo"))
            .apply(RequestOptions.circleCropTransform())
            .submit()

        val photo = futureTarget.get()

        GlideApp.with(context).clear(futureTarget)


        mNotificationBuilder.setSmallIcon(R.drawable.ic_notification_logo)
            .setColor(context.resources.getColor(R.color.colorPrimary))
            .setContentTitle(title)
            .setLargeIcon(photo)
            .setAutoCancel(true)
            .setStyle(NotificationCompat.BigTextStyle().bigText(Html.fromHtml(body)))



        return super.setPayload(data)
    }
}