package com.autolink.sayaradz.repository

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import com.autolink.sayaradz.api.SayaraDzApi
import com.autolink.sayaradz.repository.brand.BrandsDataSource
import com.autolink.sayaradz.repository.models.ModelsDataSource
import com.autolink.sayaradz.vo.CarDriver
import com.google.android.gms.auth.api.credentials.IdToken
import com.google.firebase.auth.FirebaseAuth
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import java.util.concurrent.Executor

enum class DataSourceKey private constructor(s: String) {
    Brands("Brands"),
    Models("Modles")
}


class DataSourceFactory<T:Any>(val api:SayaraDzApi,
                               private val networkExecutor:Executor,
                               private val dataSourceKey: DataSourceKey,
                               private val compositeDisposable: CompositeDisposable,
                               private val params:HashMap<String,String>? = null):DataSource.Factory<String,T>(){

    val dataSourceLiveData  = MutableLiveData<BaseDataSource<T>>()


    override fun create(): DataSource<String, T> {
        val dataSource: BaseDataSource<T> = when(dataSourceKey){
            DataSourceKey.Brands -> BrandsDataSource(api,networkExecutor,compositeDisposable) as BaseDataSource<T>
            DataSourceKey.Models -> ModelsDataSource(params!!["brandId"]!!,api,networkExecutor,compositeDisposable) as BaseDataSource<T>
        }
        dataSourceLiveData.postValue(dataSource)
        return dataSource
    }
}