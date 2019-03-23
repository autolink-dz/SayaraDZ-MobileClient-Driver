package com.autolink.sayaradz.viewmodel

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.autolink.sayaradz.repository.tariff.TariffRepository
import com.autolink.sayaradz.repository.utils.Status
import com.autolink.sayaradz.util.Event
import com.autolink.sayaradz.vo.Order
import com.autolink.sayaradz.vo.Vehicle
import com.autolink.sayaradz.vo.Version
import io.reactivex.disposables.CompositeDisposable

class TariffViewModel(private val tariffRepository: TariffRepository):ViewModel(){

     private val compositeDisposable = CompositeDisposable()
     private val vehicle = MutableLiveData<MutableList<Vehicle>>()
     private val colorPrice = MutableLiveData<Float>()
     private val versionPrice = MutableLiveData<Float>()
     private val orderState:MutableLiveData<Event<Status>> = MutableLiveData()

     private lateinit var mVersion:Version

     val colorPriceLiveData  = Transformations.map(colorPrice){
        it
    }
     val versionPriceLiveData = Transformations.map(versionPrice){
        it
    }
     val vehicleAvailability:LiveData<Boolean> = Transformations.map(vehicle){
         Log.d("TAG","cheking the list")
         it.isNotEmpty()
     }
     val orderStateLiveData:LiveData<Event<Status>> = Transformations.map(orderState){
        it
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

        vehicle.postValue(mutableListOf())

        tariffRepository.getItemPrice(TariffRepository.Item.Colors, mVersion.brandId,mVersion.modelCode,colorCode)
            .flatMap {
                colorPrice.postValue(it)
                tariffRepository.getVehicule(mVersion.brandId,mVersion.modelCode,mVersion.code,colorCode)
            }
            .subscribe({
                vehicle.postValue(it.data.toMutableList())
            },{
                vehicle.postValue(mutableListOf())
            })
    }


    @SuppressLint("CheckResult")
    fun setOrder(){
        tariffRepository.setOrder(mVersion.brandId, vehicle.value!![0].id, (colorPriceLiveData.value!! + versionPriceLiveData.value!!))
            .subscribe({

                val updatedList = vehicle.value!!
                updatedList.removeAt(0)
                vehicle.postValue(updatedList)

                orderState.postValue(Event(Status.SUCCESS))
            },{
                Log.d("TAG","an error occured ${it.message}")
                orderState.postValue(Event(Status.FAILED))
            })
    }


    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }

}