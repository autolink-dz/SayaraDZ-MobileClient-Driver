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
import com.autolink.sayaradz.vo.Announcement
import com.autolink.sayaradz.vo.CarDriver
import io.reactivex.disposables.CompositeDisposable

class AnnouncementsViewModel(private val announcementsRepository: AnnouncementsRepository):ViewModel(){

    private val compositeDisposable = CompositeDisposable()



    init {
        announcementsRepository.compositeDisposable  = compositeDisposable
    }

    private val repoResult = announcementsRepository.getAnnouncements()
    private val offerState:MutableLiveData<Event<Status>> = MutableLiveData()


    val announcementsList = Transformations.switchMap(repoResult) {
        it.pagedList
    }!!
    val networkState = Transformations.switchMap(repoResult) {
        it.networkState }
    val refreshState = Transformations.switchMap(repoResult) {
        it.refreshState }
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
        Log.d("Test","the object is ${announcement.owner}")
        announcementsRepository.setOffer(announcement.id,announcement.owner.id,carDriver.id,price)
            .subscribe({
                offerState.postValue(Event(Status.SUCCESS))
            },{
                Log.d("TAG","an error occured ${it.message}")
                offerState.postValue(Event(Status.FAILED))
            })

    }




    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }

}