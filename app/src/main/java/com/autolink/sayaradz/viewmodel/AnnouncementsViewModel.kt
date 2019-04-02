package com.autolink.sayaradz.viewmodel

import android.util.Log
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.autolink.sayaradz.repository.announcement.AnnouncementsRepository
import io.reactivex.disposables.CompositeDisposable

class AnnouncementsViewModel(private val announcementsRepository: AnnouncementsRepository):ViewModel(){

    private val compositeDisposable = CompositeDisposable()



    init {
        announcementsRepository.compositeDisposable  = compositeDisposable
    }

    private val repoResult = announcementsRepository.getAnnouncements()

    val announcementsList = Transformations.switchMap(repoResult) {
        it.pagedList
    }!!

    val networkState = Transformations.switchMap(repoResult) { it.networkState }
    val refreshState = Transformations.switchMap(repoResult) { it.refreshState }



    fun refresh() {
        repoResult.value?.refresh?.invoke()
    }

    fun retry() {
        val listing = repoResult.value
        listing?.retry?.invoke()
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }

}