package com.autolink.sayaradz.repository.announcement

import android.annotation.SuppressLint
import android.util.Log
import com.autolink.sayaradz.api.SayaraDzApi
import com.autolink.sayaradz.repository.BaseDataSource
import com.autolink.sayaradz.repository.utils.NetworkState
import com.autolink.sayaradz.vo.Announcement
import com.autolink.sayaradz.vo.Brand
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.Executor

class AnnouncementsDataSource(api: SayaraDzApi,
                              networkExecutor: Executor,
                              compositeDisposable: CompositeDisposable): BaseDataSource<Announcement>(api,networkExecutor,compositeDisposable){

    @SuppressLint("CheckResult")
    override fun loadInitial(params: LoadInitialParams<String>, callback: LoadInitialCallback<String, Announcement>) {
        networkState.postValue(NetworkState.LOADING)
        initialLoad.postValue(NetworkState.LOADING)



        api.getAnnouncements()
            .subscribeOn(Schedulers.from(networkExecutor))
            .observeOn(Schedulers.from(networkExecutor))
            .doOnSubscribe {
                compositeDisposable.add(it)
            }
            .subscribe({ listing ->
                val key = listing.key
                val data = listing.data
                val extras = listing.extras

                Log.d("CASE","extras $extras")
                Log.d("CASE","data $data")
                val announcements = data.map {
                  with(it){
                       Announcement(
                            id,
                            photoURL,
                            price,
                            year,
                            date,
                            distance,
                            extras.carDrivers.getValue(ownerId),
                            extras.brands.getValue(brandId),
                            extras.models.getValue(modelId),
                            extras.versions.getValue(versionId))
                    }
                }

                retry = null
                networkState.postValue(NetworkState.LOADED)
                initialLoad.postValue(NetworkState.LOADED)
                callback.onResult(announcements,"0",key)
            },{
                retry = {
                    loadInitial(params,callback) }

                val error  = NetworkState.error(it.message)
                networkState.postValue(error)
                initialLoad.postValue(error)
                Log.d("CASE","error ${it.message}")
            })


    }

    @SuppressLint("CheckResult")
    override fun loadAfter(params: LoadParams<String>, callback: LoadCallback<String, Announcement>) {
        networkState.postValue(NetworkState.LOADING)

        api.getAnnouncements(params.key)
            .subscribeOn(Schedulers.from(networkExecutor))
            .observeOn(Schedulers.from(networkExecutor))
            .doOnSubscribe {
                compositeDisposable.add(it)
            }
            .subscribe({ listing ->

                val key = listing.key
                val data = listing.data

                val extras = listing.extras

                val announcements = data.map {
                    with(it){
                        Announcement(
                            id,
                            photoURL,
                            price,
                            year,
                            date,
                            distance,
                            extras.carDrivers.getValue(ownerId),
                            extras.brands.getValue(brandId),
                            extras.models.getValue(modelId),
                            extras.versions.getValue(versionId))
                    }
                }

                retry = null
                callback.onResult(announcements,key)
                networkState.postValue(NetworkState.LOADED)

            },{
                retry = {
                    loadAfter(params,callback) }

                val error  = NetworkState.error(it.message)
                networkState.postValue(error)
            })
    }

    override fun loadBefore(params: LoadParams<String>, callback: LoadCallback<String, Announcement>) {}
}