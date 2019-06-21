package com.autolink.sayaradz.viewmodel

import android.annotation.SuppressLint
import android.util.Log
import androidx.databinding.BaseObservable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.autolink.sayaradz.repository.announcement.AnnouncementsRepository
import com.autolink.sayaradz.repository.utils.Status
import com.autolink.sayaradz.util.Event
import com.autolink.sayaradz.vo.Announcement
import com.autolink.sayaradz.vo.Brand
import com.autolink.sayaradz.vo.CarDriver
import io.reactivex.disposables.CompositeDisposable

class AnnouncementsViewModel(private val announcementsRepository: AnnouncementsRepository):ViewModel(){

    private val compositeDisposable = CompositeDisposable()
    companion object {
        const val MAX_PRICE = 35000000F
        const val MIN_PRICE = 1000000F
        const val MAX_DISTANCE = 500000F
        const val MIN_DISTANCE = 0F
        const val PRICE_PREFIX = 1000000F
        const val DISTANCE_PREFIX = 1000F
    }


    init {
        announcementsRepository.compositeDisposable  = compositeDisposable
        announcementsRepository.setConstraints(Pair(MIN_PRICE, MAX_PRICE),Pair(MIN_DISTANCE,MAX_DISTANCE), mutableListOf())
    }


    private val repoResult = announcementsRepository.getAnnouncements()
    private val offerState:MutableLiveData<Event<Status>> = MutableLiveData()


    val announcementsList = Transformations.switchMap(repoResult) {
        it.pagedList
    }!!
    val networkState = Transformations.switchMap(repoResult) {
        it.networkState }!!
    val refreshState = Transformations.switchMap(repoResult) {
        it.refreshState
    }!!
    val offerStateLiveData:LiveData<Event<Status>> = Transformations.map(offerState){
        it
    }




    fun refresh() {
        repoResult.value?.refresh?.invoke()
    }
    fun retry() {
        val listing = repoResult.value
        listing?.retry?.invoke()
    }

    @SuppressLint("CheckResult")
    fun setOffer(announcement:Announcement, carDriver: CarDriver, price:Float){
        announcementsRepository.setOffer(announcement.id,announcement.owner.id,carDriver.id,price)
            .subscribe({
                offerState.postValue(Event(Status.SUCCESS))
            },{
                offerState.postValue(Event(Status.FAILED))
            })

    }



    fun setConstraints(priceRange:Pair<Float,Float>,distanceRange:Pair<Float,Float>,brands:List<Brand>){
        announcementsRepository.setConstraints(priceRange,distanceRange,brands)
        refresh()
    }
    fun getPriceConstraints() = announcementsRepository.priceRange
    fun getBrandsConstraints() = announcementsRepository.brands
    fun getDistanceConstraints() = announcementsRepository.distanceRange

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }

}