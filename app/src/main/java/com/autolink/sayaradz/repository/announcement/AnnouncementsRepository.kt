package com.autolink.sayaradz.repository.announcement

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.paging.toLiveData
import com.autolink.sayaradz.api.SayaraDzApi
import com.autolink.sayaradz.repository.DataSourceFactory
import com.autolink.sayaradz.repository.DataSourceKey
import com.autolink.sayaradz.repository.IRepository
import com.autolink.sayaradz.repository.utils.Listing
import com.autolink.sayaradz.vo.Announcement
import com.autolink.sayaradz.vo.Brand
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.Executor

class AnnouncementsRepository(private val api: SayaraDzApi,
                              override val networkExecutor: Executor,
                              override val diskExecutor: Executor
): IRepository {

    lateinit var compositeDisposable: CompositeDisposable

    enum class OfferState(val value:String){
        ACCEPTED("2"),
        REJECTED("0")
    }

    companion object {
        private const val DEFAULT_PAGE_SIZE = 20
    }

    fun getAnnouncements(pageSize: Int = DEFAULT_PAGE_SIZE): LiveData<Listing<Announcement>> {

        val listing = MutableLiveData<Listing<Announcement>>()

        Log.d("AnnouncementsRepository","getAnnouncements")

        val sourceFactory = DataSourceFactory<Announcement>(
            api,
            networkExecutor,
            DataSourceKey.Announcements,
            compositeDisposable
        )

        val livePagedList = sourceFactory.toLiveData(
            pageSize = pageSize,
            // provide custom executor for network requests, otherwise it will default to
            // Arch Components' IO pool which is also used for disk access
            fetchExecutor = networkExecutor
        )

        val refreshState = Transformations.switchMap(sourceFactory.dataSourceLiveData) {
            it.initialLoad
        }

        listing.postValue(
            Listing(
                pagedList = livePagedList,
                networkState = Transformations.switchMap(sourceFactory.dataSourceLiveData) {
                    it.networkState
                },
                retry = {
                    sourceFactory.dataSourceLiveData.value?.retryAllFailed()
                },
                refresh = {
                    sourceFactory.dataSourceLiveData.value?.invalidate()
                },
                refreshState = refreshState
            )
        )

        return listing


    }

    @SuppressLint("CheckResult")
    fun setOffer(announcementId:String, ownerID:String, clientId:String, price:Float)
            =api.setOffer(announcementId,ownerID,clientId,price)
            .subscribeOn(Schedulers.from(networkExecutor))
            .observeOn(Schedulers.from(networkExecutor))
            .doOnSubscribe { compositeDisposable.add(it) }


    override fun clear() {

    }
}
