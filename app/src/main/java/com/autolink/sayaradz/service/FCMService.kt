package com.autolink.sayaradz.service

import android.util.Log
import com.autolink.sayaradz.util.writeToSharedPreference
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


class FCMService:FirebaseMessagingService(){

    companion object {
        private const val TAG  = "FCMService"
        const val TOKEN_KEY = "INSTANCE_ID"
    }


    override fun onMessageReceived(p0: RemoteMessage?) {
        super.onMessageReceived(p0)
    }

    override fun onNewToken(p0: String?) {
        super.onNewToken(p0)
        Log.d(TAG, "a new token is generated")
        if(p0 != null )   writeToSharedPreference("INSTANCE_ID",p0)
    }
}