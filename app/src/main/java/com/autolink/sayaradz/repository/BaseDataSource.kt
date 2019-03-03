package com.autolink.sayaradz.repository

import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import com.autolink.sayaradz.api.SayaraDzApi
import com.autolink.sayaradz.repository.utils.NetworkState
import io.reactivex.disposables.CompositeDisposable
import java.util.concurrent.Executor

abstract class BaseDataSource<T>(protected val api:SayaraDzApi,
                                 protected val networkExecutor: Executor,
                                 val compositeDisposable: CompositeDisposable):PageKeyedDataSource<String,T>(){

    // keep a function reference for the retry event
    protected var retry: (() -> Any)? = null
    val networkState = MutableLiveData<NetworkState>()
    val initialLoad = MutableLiveData<NetworkState>()


    fun retryAllFailed() {
        val prevRetry = retry
        retry = null
        prevRetry?.let {
            networkExecutor.execute {
                it.invoke()
            }
        }
    }


    fun clear(){
        compositeDisposable.clear()
    }
}