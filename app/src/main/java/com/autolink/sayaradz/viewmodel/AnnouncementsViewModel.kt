package com.autolink.sayaradz.viewmodel

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.autolink.sayaradz.repository.announcement.AnnouncementsRepository
import com.autolink.sayaradz.repository.utils.Listing
import com.autolink.sayaradz.repository.utils.Status
import com.autolink.sayaradz.util.Event
import com.autolink.sayaradz.vo.*
import io.reactivex.disposables.CompositeDisposable
import java.util.*

class AnnouncementsViewModel(private val announcementsRepository: AnnouncementsRepository):ViewModel(){

    private val compositeDisposable = CompositeDisposable()
    companion object {
        const val MAX_PRICE = 35000000F
        const val MIN_PRICE = 1000000F
        const val MAX_DISTANCE = 500000F
        const val MIN_DISTANCE = 0F
        const val PRICE_PREFIX = 1000000F
        const val DISTANCE_PREFIX = 1000F
        const val TAG = "AnnouncementsViewModel"
    }

    private var repoResult: LiveData<Listing<Announcement>>
    private val offerState:MutableLiveData<Event<Status>> = MutableLiveData()
    private val announcementState:MutableLiveData<Event<Status>> = MutableLiveData()

    var newAnnouncementVersion = MutableLiveData<Version>()
    var newAnnouncementPrice = MutableLiveData<Int>().apply { value = AnnouncementsViewModel.MIN_PRICE.toInt() }
    var newAnnouncementYear = MutableLiveData<Int>().apply { value = 2005 }
    var newAnnouncementDistance = MutableLiveData<Int>().apply { value = MIN_DISTANCE.toInt() }
    var newAnnouncementDescription  = MutableLiveData<String>()
    var newAnnouncementPhotoUrl = MutableLiveData<String>()



    init {
        announcementsRepository.compositeDisposable  = compositeDisposable
        announcementsRepository.setConstraints(Pair(MIN_PRICE, MAX_PRICE),Pair(MIN_DISTANCE,MAX_DISTANCE), mutableListOf())
        repoResult = announcementsRepository.getAnnouncements()
        newAnnouncementDescription.value  = String()
    }





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

    val announcementStateLiveData:LiveData<Event<Status>> = Transformations.map(announcementState){
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


    @SuppressLint("CheckResult")
    fun setAnnouncement(user:CarDriver) {

       announcementState.value = Event(Status.RUNNING)
        val version = newAnnouncementVersion.value ?: return
        val announcement = CompactAnnouncement(
            id = "",
            ownerId = user.id,
            brandId =  version.brandId,
            modelId = version.modelId,
            versionId = version.id,
            photoURL = newAnnouncementPhotoUrl.value?: "",
            price = newAnnouncementPrice.value!!.toFloat(),
            year = newAnnouncementYear.value.toString(),
            date = Date(),
            distance = newAnnouncementDistance.value!!.toFloat(),
            description = newAnnouncementDescription.value.toString())

        announcementsRepository.setAnnouncement(announcement)
            .subscribe({
                    Log.d(TAG,"the announcement is posted")
                    announcementState.postValue(Event(Status.SUCCESS))
            },{
                    Log.d(TAG,"error when posting the announcement")
                    announcementState.postValue(Event(Status.SUCCESS))
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