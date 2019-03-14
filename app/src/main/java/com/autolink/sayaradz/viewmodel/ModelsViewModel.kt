package com.autolink.sayaradz.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.paging.PagedList
import com.autolink.sayaradz.repository.models.ModelsRepository
import com.autolink.sayaradz.repository.utils.Listing
import com.autolink.sayaradz.repository.utils.NetworkState
import com.autolink.sayaradz.vo.Model
import io.reactivex.disposables.CompositeDisposable

class ModelsViewModel(private val modelsRepository: ModelsRepository): ViewModel(){

    private val compositeDisposable = CompositeDisposable()

    private val brandId  = MutableLiveData<String>()

    private val repoResult:LiveData<Listing<Model>> = Transformations.switchMap(brandId){
        modelsRepository.getModels(brandId = it)
    }

    val modelsList: LiveData<PagedList<Model>> by lazy {
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
        modelsRepository.compositeDisposable  = compositeDisposable
    }




    fun refresh() {
        repoResult.value?.refresh?.invoke()
    }

    fun retry() {
        val listing = repoResult.value
        listing?.retry?.invoke()
    }

    fun setBrandId(id:String){
        if(id == this.brandId.value) return
        this.brandId.value = id
    }



    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}