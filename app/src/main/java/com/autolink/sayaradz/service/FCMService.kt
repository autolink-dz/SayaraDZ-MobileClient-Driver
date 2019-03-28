package com.autolink.sayaradz.service

import android.util.Log
import androidx.core.app.NotificationManagerCompat
import androidx.work.*
import com.autolink.sayaradz.notification.NotificationMangerFactory
import com.autolink.sayaradz.util.writeToSharedPreference
import com.autolink.sayaradz.worker.NotificationWorker
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson


class FCMService:FirebaseMessagingService(){

    companion object {
        private const val TAG  = "FCMService"
        const val TOKEN_KEY = "INSTANCE_ID"
    }


    override fun onMessageReceived(p0: RemoteMessage?) {


        val remoteMessage = p0 ?: return
        val channelId = remoteMessage.data.getValue("android_channel_id") ?: return
        val data = Gson().toJson(remoteMessage.data)


        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val userData = workDataOf(NotificationWorker.CHANNEL_ID_KEY to channelId,
                                                NotificationWorker.NOTIFICATION_DATA_KEY to data )


        val instanceIdTokenUploadWorker = OneTimeWorkRequestBuilder<NotificationWorker>()
            .setConstraints(constraints)
            .setInputData(userData)
            .build()

        WorkManager.getInstance().enqueue(instanceIdTokenUploadWorker)

    }

    override fun onNewToken(p0: String?) {
        super.onNewToken(p0)
        Log.d(TAG, "a new instanceToken is generated $p0")
        if(p0 != null )   writeToSharedPreference("INSTANCE_ID",p0)
    }
}