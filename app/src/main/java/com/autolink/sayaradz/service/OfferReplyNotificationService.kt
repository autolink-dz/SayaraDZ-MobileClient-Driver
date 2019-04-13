package com.autolink.sayaradz.service

import android.app.IntentService
import android.content.Intent
import android.util.Log
import androidx.work.*
import com.autolink.sayaradz.repository.announcement.AnnouncementsRepository
import com.autolink.sayaradz.worker.OfferNotificationReplyWorker

class OfferReplyNotificationService:IntentService("OfferReplyNotificationService"){

    companion object {
        const val OFFER_ID_KEY = "OFFER_ID_KEY"
        const val ACCEPT_OFFER_ACTION_KEY  = "ACCEPT_OFFER_ACTION_KEY"
        const val REJECT_OFFER_ACTION_KEY  = "REJECT_OFFER_ACTION_KEY"
    }

    override fun onHandleIntent(p0: Intent?) {

        val action  = p0?.action
        val extras  = p0?.extras

        val offerId = extras?.getString(OFFER_ID_KEY)
        val state = when(action){
            ACCEPT_OFFER_ACTION_KEY -> AnnouncementsRepository.OfferState.ACCEPTED.value
            REJECT_OFFER_ACTION_KEY ->AnnouncementsRepository.OfferState.REJECTED.value
            else -> return
        }


        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val userData = workDataOf(OfferNotificationReplyWorker.OFFER_ID_KEY to offerId,
            OfferNotificationReplyWorker.OFFER_STATE_KEY to state )


        val instanceIdTokenUploadWorker = OneTimeWorkRequestBuilder<OfferNotificationReplyWorker>()
            .setConstraints(constraints)
            .setInputData(userData)
            .build()

        WorkManager.getInstance().enqueue(instanceIdTokenUploadWorker)
        stopSelf()



    }



}