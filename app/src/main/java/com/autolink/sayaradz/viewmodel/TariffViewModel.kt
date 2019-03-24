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
import com.autolink.sayaradz.vo.Option
import com.autolink.sayaradz.vo.Vehicle
import com.autolink.sayaradz.vo.Version
import io.reactivex.disposables.CompositeDisposable

class TariffViewModel(private val tariffRepository: TariffRepository):ViewModel(){

     private val compositeDisposable = CompositeDisposable()

     private val vehicle = MutableLiveData<MutableList<Vehicle>>()

     private val colorPrice = MutableLiveData<Float>()
     private val versionPrice = MutableLiveData<Float>()

     private val suggestedOptionsPrice = MutableLiveData<Map<String,Int>>()
     private val suggestedOptionsState = MutableLiveData<MutableMap<String,Boolean>>()

     private val orderState:MutableLiveData<Event<Status>> = MutableLiveData()

     private lateinit var mVersion:Version
     private lateinit var mColorCode:String
     private lateinit var mSelectedOptions:List<Option>


     val suggestedOptionsPriceLiveData: LiveData<Map<String, Int>> = Transformations.map(suggestedOptionsPrice){
         it
     }
     val colorPriceLiveData: LiveData<Int> = Transformations.map(colorPrice){
        it.toInt()
    }
     val versionPriceLiveData: LiveData<Int> = Transformations.map(versionPrice){
        it.toInt()
    }

    val suggestedOptionTotalPrice:LiveData<Int> = Transformations.map(suggestedOptionsState){
        var total = 0
        suggestedOptionsPrice.value?.filter {entry ->
            it.containsKey(entry.key) && it[entry.key] == true
        }?.forEach{
            total+=it.value
        }
        total
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
        mSelectedOptions = mVersion.options
        tariffRepository.getItemTariff(TariffRepository.Item.Version, version.brandId,version.modelCode,version.code)
            .flatMapIterable {
                versionPrice.postValue(it.price)
                mVersion.nonSupportedOptions
            }
            .flatMap {
                tariffRepository.getItemTariff(TariffRepository.Item.Option, version.brandId,version.modelCode,it.code) }
            .toList()
            .subscribe({ tariffs ->
                val suggestedOptionsStateMap = tariffs.map { it.code to false }.toMap().toMutableMap()
                val suggestedOptionsPriceMap = tariffs.map { it.code to it.price.toInt() }.toMap()

                suggestedOptionsState.postValue(suggestedOptionsStateMap)
                suggestedOptionsPrice.postValue(suggestedOptionsPriceMap)

            },{
                Log.d("TAG","an error occured ${it.message}")
            })
    }

    @SuppressLint("CheckResult")
    fun setColorCode(colorCode: String){

        vehicle.postValue(mutableListOf())
        mColorCode = colorCode
        tariffRepository.getItemTariff(TariffRepository.Item.Colors, mVersion.brandId,mVersion.modelCode,colorCode)
            .flatMap {
                colorPrice.postValue(it.price)
                tariffRepository.getVehicule(mVersion.brandId,mVersion.modelCode,mVersion.code,colorCode,mSelectedOptions)
            }
            .subscribe({
                vehicle.postValue(it.data.toMutableList())
            },{
                vehicle.postValue(mutableListOf())
            })
    }


    @SuppressLint("CheckResult")
    fun setSuggestedOption(optionCode:String, state:Boolean){

        val updatedMap  = suggestedOptionsState.value !!
        updatedMap[optionCode] = state

        mSelectedOptions = mVersion.nonSupportedOptions.filter {  updatedMap[it.code] == true }
                                                                     .toMutableList()
                                                                     .apply { addAll(mVersion.options) }


        tariffRepository.getVehicule(mVersion.brandId,mVersion.modelCode,mVersion.code,mColorCode,mSelectedOptions)
            .subscribe({
                suggestedOptionsState.postValue(updatedMap)
                vehicle.postValue(it.data.toMutableList())
            },{
                vehicle.postValue(mutableListOf())
            })
    }

    @SuppressLint("CheckResult")
    fun setOrder(){
        tariffRepository.setOrder(mVersion.brandId, vehicle.value!![0].id, (colorPrice.value!! + versionPrice.value!!))
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