package com.autolink.sayaradz.notification.manager

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.text.Html
import android.util.Log
import androidx.core.app.NotificationCompat
import com.autolink.sayaradz.R
import com.autolink.sayaradz.notification.NotificationChannels
import com.autolink.sayaradz.notification.NotificationManager
import com.autolink.sayaradz.service.OfferReplyNotificationService
import com.autolink.sayaradz.util.GlideApp
import com.autolink.sayaradz.util.GlideRequest
import com.bumptech.glide.request.RequestOptions

class OffersNotificationManager(context: Context):NotificationManager(context) {

    companion object {
        private const val TAG = "OffersNotificationMg"
    }


    override fun setPayload(data: Map<String, String>):String{

        mNotificationBuilder = NotificationCompat.Builder(context, NotificationChannels.OFFERS_CHANNEL.key)

        var title = ""
        var body = ""

        title = context.getString(R.string.notification_offer_title)
        body = context.getString(R.string.notification_offer_body, data.getValue("client"),data.getValue("price"),data.getValue("version"))

        val futureTarget = GlideApp.with(context)
            .asBitmap()
            .load(data.getValue("photo"))
            .apply(RequestOptions.circleCropTransform())
            .submit()

        val photo = futureTarget.get()

        GlideApp.with(context).clear(futureTarget)
        val offerId = data.getValue("offerId")

        val positiveReplyIntent  = Intent(context,OfferReplyNotificationService::class.java).apply {
            putExtra(OfferReplyNotificationService.OFFER_ID_KEY,offerId)
            action = OfferReplyNotificationService.ACCEPT_OFFER_ACTION_KEY
        }

        val negativeReplyIntent = Intent(context,OfferReplyNotificationService::class.java).apply {
            putExtra(OfferReplyNotificationService.OFFER_ID_KEY,offerId)
            action = OfferReplyNotificationService.REJECT_OFFER_ACTION_KEY
        }

        Log.d("TAG","this event will change the offer with the ID $offerId")

        mNotificationBuilder.setSmallIcon(R.drawable.ic_notification_logo)
            .setColor(context.resources.getColor(R.color.colorPrimary))
            .setContentTitle(title)
            .setLargeIcon(photo)
            .setAutoCancel(true)
            .setStyle(NotificationCompat.BigTextStyle().bigText(Html.fromHtml(body)))
            .addAction(R.drawable.ic_done,context.getString(R.string.accept_title), PendingIntent.getService(context,1,positiveReplyIntent,PendingIntent.FLAG_ONE_SHOT))
            .addAction(R.drawable.ic_clear,context.getString(R.string.reject_title), PendingIntent.getService(context,2,negativeReplyIntent,PendingIntent.FLAG_ONE_SHOT))

        return super.setPayload(data)
    }

}