package com.autolink.sayaradz.repository.brand

import android.annotation.SuppressLint
import com.autolink.sayaradz.api.SayaraDzApi
import com.autolink.sayaradz.repository.utils.NetworkState
import com.autolink.sayaradz.repository.BaseDataSource
import com.autolink.sayaradz.vo.Brand
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.Executor

class BrandsDataSource( api:SayaraDzApi,
                        networkExecutor:Executor,
                        compositeDisposable: CompositeDisposable):
    BaseDataSource<Brand>(api,networkExecutor,compositeDisposable){


    @SuppressLint("CheckResult")
    override fun loadInitial(params: LoadInitialParams<String>, callback: LoadInitialCallback<String, Brand>) {

        networkState.postValue(NetworkState.LOADING)
        initialLoad.postValue(NetworkState.LOADING)

        api.GetBrandsList()
            .subscribeOn(Schedulers.from(networkExecutor))
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
    override fun loadAfter(params: LoadParams<String>, callback: LoadCallback<String, Brand>) {
        networkState.postValue(NetworkState.LOADING)

        api.GetBrandsList(params.key)
            .subscribeOn(Schedulers.from(networkExecutor))
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

    override fun loadBefore(params: LoadParams<String>, callback: LoadCallback<String, Brand>) {
    }

}
