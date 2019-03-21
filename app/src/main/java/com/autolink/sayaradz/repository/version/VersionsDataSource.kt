package com.autolink.sayaradz.repository.version

import android.annotation.SuppressLint
import com.autolink.sayaradz.api.SayaraDzApi
import com.autolink.sayaradz.repository.BaseDataSource
import com.autolink.sayaradz.repository.utils.NetworkState
import com.autolink.sayaradz.vo.Version
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.Executor

class VersionsDataSource(val modelId:String,
                         api: SayaraDzApi,
                         networkExecutor: Executor,
                         compositeDisposable: CompositeDisposable
): BaseDataSource<Version>(api,networkExecutor,compositeDisposable){

    @SuppressLint("CheckResult")
    override fun loadInitial(params: LoadInitialParams<String>, callback: LoadInitialCallback<String, Version>) {
        networkState.postValue(NetworkState.LOADING)
        initialLoad.postValue(NetworkState.LOADING)

        api.getVersionsList(modelId=modelId)
            .subscribeOn(Schedulers.from(networkExecutor))
            .observeOn(Schedulers.from(networkExecutor))
            .doOnSubscribe {
                compositeDisposable.add(it)
            }
            .subscribe({
                val key = it.key
                val data = it.data
                retry = null
                networkState.postValue(NetworkState.LOADED)
                initialLoad.postValue(NetworkState.LOADED)
                callback.onResult(data,"0",key)
            },{
                retry = {
                    loadInitial(params,callback) }

                val error  = NetworkState.error(it.message)
                networkState.postValue(error)
                initialLoad.postValue(error)
            })
    }

    @SuppressLint("CheckResult")
    override fun loadAfter(params: LoadParams<String>, callback: LoadCallback<String, Version>) {
        networkState.postValue(NetworkState.LOADING)

        api.getVersionsList(key = params.key, modelId= modelId)
            .subscribeOn(Schedulers.from(networkExecutor))
            .observeOn(Schedulers.from(networkExecutor))
            .doOnSubscribe {
                compositeDisposable.add(it)
            }
            .subscribe({

                val key = it.key
                val data = it.data
                retry = null
                callback.onResult(data,key)
                networkState.postValue(NetworkState.LOADED)

            },{
                retry = {
                    loadAfter(params,callback) }

                val error  = NetworkState.error(it.message)
                networkState.postValue(error)
            })
    }


    override fun loadBefore(params: LoadParams<String>, callback: LoadCallback<String, Version>) {}

}