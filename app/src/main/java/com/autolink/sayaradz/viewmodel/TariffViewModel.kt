package com.autolink.sayaradz.viewmodel

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.autolink.sayaradz.repository.tariff.TariffRepository
import com.autolink.sayaradz.vo.Vehicle
import com.autolink.sayaradz.vo.Version
import io.reactivex.disposables.CompositeDisposable

class TariffViewModel(private val tariffRepository: TariffRepository):ViewModel(){

     private val compositeDisposable = CompositeDisposable()
     private val vehicle = MutableLiveData<List<Vehicle>>()
     private val colorPrice = MutableLiveData<Float>()
     private val versionPrice = MutableLiveData<Float>()
     private lateinit var mVersion:Version

     val colorPriceLiveData  = Transformations.map(colorPrice){
        it.toInt()
    }
     val versionPriceLiveData = Transformations.map(versionPrice){
        it.toInt()
    }
     val orderState:LiveData<Boolean> = Transformations.map(vehicle){
         it.isNotEmpty()
     }

     init {
        tariffRepository.compositeDisposable = compositeDisposable
    }


    @SuppressLint("CheckResult")
    fun setVersion(version:Version){
        mVersion = version
        tariffRepository.getItemPrice(TariffRepository.Item.Version, version.brandId,version.modelCode,version.code)
            .subscribe({
                    Log.d("TAG","the version  price is $it")
                    versionPrice.postValue(it)
            },{
                Log.d("TAG","an error occured ${it.message}")
            })
    }

    @SuppressLint("CheckResult")
    fun setColorCode(colorCode: String){

        vehicle.postValue(listOf())

        tariffRepository.getItemPrice(TariffRepository.Item.Colors, mVersion.brandId,mVersion.modelCode,colorCode)
            .flatMap {
                colorPrice.postValue(it)
                tariffRepository.getVehicule(mVersion.brandId,mVersion.modelCode,mVersion.code,colorCode)
            }
            .subscribe({
                vehicle.postValue(it.data)
            },{
                vehicle.postValue(listOf())
            })
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }

}