package com.autolink.sayaradz.worker

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.autolink.sayaradz.api.SayaraDzApi
import com.autolink.sayaradz.service.FCMService
import com.autolink.sayaradz.util.DefaultServiceLocator
import com.autolink.sayaradz.util.ServiceLocator
import com.autolink.sayaradz.util.readFromSharedPreference

open class InstanceIdTokenUploadWorker(appContext: Context,
                                  workerParams: WorkerParameters): Worker(appContext, workerParams) {

companion object {
    const val USER_UID_KEY = "UID"
    private const val TAG  = "InstanceIdTokenUpload"
}

    override fun doWork(): Result {
        Log.d(TAG,"InstanceIdTokenUploadWorker is starting")

        val sayaraDzApi = ServiceLocator.instance(applicationContext).getSayaraDzApi()
        val uid = inputData.getString(USER_UID_KEY) ?: return Result.retry()
        val token  = applicationContext.readFromSharedPreference(FCMService.TOKEN_KEY) ?: return Result.retry()

        sayaraDzApi.setUserInstanceIdToken(uid,token).execute()

        return Result.success()
    }



}