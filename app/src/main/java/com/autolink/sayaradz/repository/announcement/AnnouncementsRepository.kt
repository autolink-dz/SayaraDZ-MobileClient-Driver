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
import com.autolink.sayaradz.ui.fragment.oldcar.AnnouncementsFilterSheetFragment.Companion.BRANDS_KEY
import com.autolink.sayaradz.ui.fragment.oldcar.AnnouncementsFilterSheetFragment.Companion.MAX_DISTANCE_KEY
import com.autolink.sayaradz.ui.fragment.oldcar.AnnouncementsFilterSheetFragment.Companion.MAX_PRICE_KEY
import com.autolink.sayaradz.ui.fragment.oldcar.AnnouncementsFilterSheetFragment.Companion.MIN_DISTANCE_KEY
import com.autolink.sayaradz.ui.fragment.oldcar.AnnouncementsFilterSheetFragment.Companion.MIN_PRICE_KEY
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
    private lateinit var mSourceFactory:DataSourceFactory<Announcement>

    lateinit var priceRange:Pair<Float,Float>
    lateinit var distanceRange:Pair<Float,Float>
    lateinit var brands:List<Brand>

    enum class OfferState(val value:String){
        ACCEPTED("2"),
        REJECTED("0")
    }


    companion object {
        private const val DEFAULT_PAGE_SIZE = 20
    }



    fun setConstraints(priceRange:Pair<Float,Float>,distanceRange:Pair<Float,Float>,brands:List<Brand>){
        this.priceRange = priceRange
        this.distanceRange = distanceRange
        this.brands = brands
    }

    fun getAnnouncements(pageSize: Int = DEFAULT_PAGE_SIZE): LiveData<Listing<Announcement>> {

        val listing = MutableLiveData<Listing<Announcement>>()



        mSourceFactory =  DataSourceFactory(
            api,
            networkExecutor,
            DataSourceKey.Announcements,
            compositeDisposable,
            mapOf(
                MIN_PRICE_KEY to priceRange.first,
                MAX_PRICE_KEY to priceRange.second,
                MIN_DISTANCE_KEY to distanceRange.first,
                MAX_DISTANCE_KEY to distanceRange.second,
                BRANDS_KEY to brands
            ))


        val livePagedList = mSourceFactory.toLiveData(
            pageSize = pageSize,
            fetchExecutor = networkExecutor)

        val refreshState = Transformations.switchMap(mSourceFactory.dataSourceLiveData) {
            it.initialLoad
        }

        listing.postValue(
            Listing(
                pagedList = livePagedList,
                networkState = Transformations.switchMap(mSourceFactory.dataSourceLiveData) {
                    it.networkState
                },
                retry = {
                    mSourceFactory.dataSourceLiveData.value?.retryAllFailed()
                },
                refresh = {
                    mSourceFactory.params=  mapOf(
                        MIN_PRICE_KEY to priceRange.first,
                        MAX_PRICE_KEY to priceRange.second,
                        MIN_DISTANCE_KEY to distanceRange.first,
                        MAX_DISTANCE_KEY to distanceRange.second,
                        BRANDS_KEY to brands)
                    mSourceFactory.dataSourceLiveData.value?.invalidate()
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
