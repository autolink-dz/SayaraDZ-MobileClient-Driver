package com.autolink.sayaradz.worker

import android.content.Context
import android.util.Log
import androidx.core.app.NotificationManagerCompat
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.autolink.sayaradz.util.ServiceLocator

class OfferNotificationReplyWorker(appContext: Context,
                                   workerParams: WorkerParameters): Worker(appContext, workerParams) {

    companion object {
        private const val TAG  = "OfferNotificationReplyWorker"
        const val OFFER_ID_KEY = "OFFER_ID_KEY"
        const val OFFER_STATE_KEY = "OFFER_STATE_KEY"
    }

    override fun doWork(): Result {

        val offerId = inputData.getString(OFFER_ID_KEY) ?: return Result.retry()
        val state   = inputData.getString(OFFER_STATE_KEY) ?: return Result.retry()
        val sayaraDzApi = ServiceLocator.instance(applicationContext).getSayaraDzApi()

        with(NotificationManagerCompat.from(applicationContext)) {
            cancel(offerId,0)
        }


        sayaraDzApi.setOfferState(offerId,state).execute()

        return Result.success()
}



}