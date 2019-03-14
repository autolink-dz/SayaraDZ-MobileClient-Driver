package com.autolink.sayaradz.repository.brand

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.paging.toLiveData
import com.autolink.sayaradz.api.SayaraDzApi
import com.autolink.sayaradz.repository.DataSourceFactory
import com.autolink.sayaradz.repository.DataSourceKey
import com.autolink.sayaradz.repository.IRepository
import com.autolink.sayaradz.repository.utils.Listing
import com.autolink.sayaradz.vo.Brand
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import java.util.concurrent.Executor

class BrandsRepository(private val api:SayaraDzApi,
                       override val networkExecutor: Executor,
                       override val diskExecutor:Executor): IRepository {

    lateinit var compositeDisposable: CompositeDisposable

    companion object {
        private const val DEFAULT_PAGE_SIZE  = 20
    }
    fun getBrands(pageSize :Int = DEFAULT_PAGE_SIZE):LiveData<Listing<Brand>>{

        val listing = MutableLiveData<Listing<Brand>>()

        val sourceFactory = DataSourceFactory<Brand>(
            api,
            networkExecutor,
            DataSourceKey.Brands,
            compositeDisposable
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