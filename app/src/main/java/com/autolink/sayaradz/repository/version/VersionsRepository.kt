package com.autolink.sayaradz.repository.version

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
import com.autolink.sayaradz.vo.Version
import io.reactivex.disposables.CompositeDisposable
import java.util.concurrent.Executor


class VersionsRepository(private val api: SayaraDzApi,
                         override val networkExecutor: Executor,
                         override val diskExecutor: Executor
): IRepository {

    lateinit var compositeDisposable: CompositeDisposable

    companion object {
        private const val DEFAULT_PAGE_SIZE  = 20
    }

    fun getVersions(modelId:String,pageSize :Int = DEFAULT_PAGE_SIZE): LiveData<Listing<Version>> {

        val listing = MutableLiveData<Listing<Version>>()
        val params  = hashMapOf<String,String>()

        params["modelId"] = modelId

        val sourceFactory = DataSourceFactory<Version>(
            api,
            networkExecutor,
            DataSourceKey.Versions,
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