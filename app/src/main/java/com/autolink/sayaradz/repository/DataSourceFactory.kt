package com.autolink.sayaradz.repository

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import com.autolink.sayaradz.api.SayaraDzApi
import com.autolink.sayaradz.repository.DataSourceKey.*
import com.autolink.sayaradz.repository.brand.BrandsDataSource
import com.autolink.sayaradz.repository.model.ModelsDataSource
import com.autolink.sayaradz.repository.version.VersionsDataSource
import io.reactivex.disposables.CompositeDisposable
import java.util.concurrent.Executor

enum class DataSourceKey private constructor(s: String) {
    Brands("Brands"),
    Models("Models"),
    Versions("Versions")
}


class DataSourceFactory<T:Any>(val api:SayaraDzApi,
                               private val networkExecutor:Executor,
                               private val dataSourceKey: DataSourceKey,
                               private val compositeDisposable: CompositeDisposable,
                               private val params:HashMap<String,String>? = null):DataSource.Factory<String,T>(){

    val dataSourceLiveData  = MutableLiveData<BaseDataSource<T>>()


    override fun create(): DataSource<String, T> {
        val dataSource: BaseDataSource<T> = when(dataSourceKey){
            Brands -> BrandsDataSource(api,networkExecutor,compositeDisposable) as BaseDataSource<T>
            Models -> ModelsDataSource(params!!["brandId"]!!,api,networkExecutor,compositeDisposable) as BaseDataSource<T>
            Versions ->  VersionsDataSource(params!!["modelId"]!!,api,networkExecutor,compositeDisposable) as BaseDataSource<T>
        }
        dataSourceLiveData.postValue(dataSource)
        return dataSource
    }
}