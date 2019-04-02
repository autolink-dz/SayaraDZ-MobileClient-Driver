package com.autolink.sayaradz.worker

import android.content.Context
import android.util.Log
import androidx.core.app.NotificationManagerCompat
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.autolink.sayaradz.notification.NotificationMangerFactory
import com.autolink.sayaradz.service.FCMService
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class NotificationWorker(appContext: Context,
                         workerParams: WorkerParameters): Worker(appContext, workerParams){


    companion object {
        const val CHANNEL_ID_KEY = "CHANNEL_ID"
        const val NOTIFICATION_DATA_KEY = "NOTIFICATION_DATA"
        private const val TAG  = "NotificationWorker"
    }

    override fun doWork(): Result {

        val channelId = inputData.getString(CHANNEL_ID_KEY) ?: return Result.retry()
        val dataString = inputData.getString(NOTIFICATION_DATA_KEY) ?: return Result.retry()
        val data  =Gson().fromJson<Map<String, String>>(dataString, object : TypeToken<Map<String, String>>() {}.type)
        val notificationManager =  NotificationMangerFactory.create(applicationContext,channelId)



        notificationManager.setPayload(data)
        with(NotificationManagerCompat.from(applicationContext)) {

            notify(0,   notificationManager.build())
        }
        return Result.success()
    }
}