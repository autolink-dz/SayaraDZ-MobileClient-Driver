package com.autolink.sayaradz.viewmodel

import androidx.lifecycle.Transformations.switchMap
import androidx.lifecycle.ViewModel
import com.autolink.sayaradz.repository.brand.BrandsRepository
import io.reactivex.disposables.CompositeDisposable

class BrandsViewModel(private val brandsRepository: BrandsRepository):ViewModel(){

    private val compositeDisposable = CompositeDisposable()


    init {
        brandsRepository.compositeDisposable  = compositeDisposable
    }

    private val repoResult = brandsRepository.getBrands()

    val brandsList = switchMap(repoResult) {
        it.pagedList
    }!!

    val networkState = switchMap(repoResult) { it.networkState }
    val refreshState = switchMap(repoResult) { it.refreshState }


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