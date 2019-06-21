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

class AnnouncementsDataSource(private val priceRange: Pair<Float,Float>,
                              private val distanceRange: Pair<Float,Float>,
                              private val brands:List<Brand>,
                              api: SayaraDzApi,
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

                Log.d("TRACK","the data is $data")
                Log.d("TRACK","the price range is $priceRange")
                Log.d("TRACK","the brands range is $brands")
                Log.d("TRACK","the distnace range is $distanceRange")

                val announcements = data.filter {
                        it.price in priceRange.first..priceRange.second
                    }
                    .filter {
                        it.distance in  distanceRange.first..distanceRange.second
                    }
                    .map {
                              with(it){
                                   Announcement(
                                        id,
                                        photoURL,
                                        price,
                                        year,
                                        date,
                                        distance,
                                        description,
                                        extras.carDrivers.getValue(ownerId),
                                        extras.brands.getValue(brandId),
                                        extras.models.getValue(modelId),
                                        extras.versions.getValue(versionId))
                              }
                }.filter {
                        if(brands.isEmpty())
                            true
                        else
                            brands.contains(it.brand)
                    }



                retry = null
                networkState.postValue(NetworkState.LOADED)
                initialLoad.postValue(NetworkState.LOADED)
                callback.onResult(announcements,"0",key)
            },{
                retry = {
                    loadInitial(params,callback)
                }

                val error  = NetworkState.error(it.message)
                networkState.postValue(error)
                initialLoad.postValue(error)
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

                val announcements = data.filter {
                    it.price in priceRange.first..priceRange.second
                }
                    .filter {
                        it.distance in  distanceRange.first..distanceRange.second
                    }
                    .map {
                        with(it){
                            Announcement(
                                id,
                                photoURL,
                                price,
                                year,
                                date,
                                distance,
                                description,
                                extras.carDrivers.getValue(ownerId),
                                extras.brands.getValue(brandId),
                                extras.models.getValue(modelId),
                                extras.versions.getValue(versionId))
                        }
                    }.filter {
                        if(brands.isEmpty())
                            true
                        else
                            brands.contains(it.brand)
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