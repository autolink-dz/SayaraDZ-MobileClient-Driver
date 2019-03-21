package com.autolink.sayaradz.repository.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.paging.toLiveData
import com.autolink.sayaradz.api.SayaraDzApi
import com.autolink.sayaradz.repository.DataSourceFactory
import com.autolink.sayaradz.repository.DataSourceKey
import com.autolink.sayaradz.repository.IRepository
import com.autolink.sayaradz.repository.utils.Listing
import com.autolink.sayaradz.vo.Model
import io.reactivex.disposables.CompositeDisposable
import java.util.concurrent.Executor


class ModelsRepository(private val api: SayaraDzApi,
                       override val networkExecutor: Executor,
                       override val diskExecutor: Executor): IRepository {

    lateinit var compositeDisposable: CompositeDisposable

    companion object {
        private const val DEFAULT_PAGE_SIZE  = 20
    }

    fun getModels(brandId:String,pageSize :Int = DEFAULT_PAGE_SIZE):LiveData<Listing<Model>>{

        val listing = MutableLiveData<Listing<Model>>()
        val params  = hashMapOf<String,String>()

        params["brandId"] = brandId

        val sourceFactory = DataSourceFactory<Model>(
            api,
            networkExecutor,
            DataSourceKey.Models,
            compositeDisposable,
            params
        )

        val livePagedList = sourceFactory.toLiveData(
            pageSize = pageSize,
            // provide custom executor for network requests, otherwise it will default to
            // Arch Components' IO pool which is also used for disk access
            fetchExecutor = networkExecutor)

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

    override fun clear() {

    }

}