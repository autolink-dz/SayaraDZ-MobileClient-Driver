package com.autolink.sayaradz.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.paging.PagedList
import com.autolink.sayaradz.repository.model.ModelsRepository
import com.autolink.sayaradz.repository.utils.Listing
import com.autolink.sayaradz.repository.version.VersionsRepository
import com.autolink.sayaradz.vo.Model
import com.autolink.sayaradz.vo.Version
import io.reactivex.disposables.CompositeDisposable

class VersionsViewModel(private val versionsRepository: VersionsRepository): ViewModel(){

    private val compositeDisposable = CompositeDisposable()

    private val model  = MutableLiveData<Model>()

    private val repoResult: LiveData<Listing<Version>> = Transformations.switchMap(model){
        versionsRepository.getVersions(modelId = it.id)
    }

    val versionList: LiveData<PagedList<Version>> by lazy {
        Transformations.switchMap(repoResult) {
            it.pagedList
        }
    }
    val networkState by lazy {
        Transformations.switchMap(repoResult) {
            it.networkState
        }

    }
    val refreshState by lazy {
        Transformations.switchMap(repoResult) {
            it.refreshState
        }
    }



    init {
        versionsRepository.compositeDisposable  = compositeDisposable
    }




    fun refresh() {
        repoResult.value?.refresh?.invoke()
    }
    fun retry() {
        val listing = repoResult.value
        listing?.retry?.invoke()
    }

    fun setModel(model:Model){
        if(model == this.model.value) return
        this.model.value = model
    }



    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}