package com.autolink.sayaradz.viewmodel

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.autolink.sayaradz.repository.user.UserRepository
import com.autolink.sayaradz.repository.utils.Status
import com.autolink.sayaradz.util.Event
import com.autolink.sayaradz.vo.CarDriver
import com.facebook.AccessToken
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.GoogleAuthProvider
import durdinapps.rxfirebase2.RxFirebaseAuth
import io.reactivex.disposables.CompositeDisposable

class UserViewModel(private val userRepository: UserRepository): ViewModel() {

    companion object {
        private const val TAG = "UserViewModel"
    }

    private val carDriver:MutableLiveData<CarDriver>  = MutableLiveData()

    private val authError:MutableLiveData<String> = MutableLiveData()

    private val subscriptionState:MutableLiveData<Event<Status>> = MutableLiveData()

    private val compositeDisposable = CompositeDisposable()


    val subscriptionStateLiveData:LiveData<Event<Status>> = Transformations.map(subscriptionState){
        it
    }

    init {
        userRepository.compositeDisposable = compositeDisposable
    }

    @SuppressLint("CheckResult")
    fun initCarDriverProfile(){
        userRepository.getAuthUser()
            .subscribe({
                Log.d(TAG,"init user profile:$it")
                carDriver.postValue(it)
            },{
                Log.d(TAG,"initCarDriverProfile ERROR: ${it.message}")
                authError.postValue(it.message)
            })
    }

    fun getCarDriverLiveData():LiveData<CarDriver> = carDriver

    fun getAuthErrorLiveData():LiveData<String> = authError

    @SuppressLint("CheckResult")
    fun signInUserWithGoogle(account:GoogleSignInAccount){

            userRepository.signInUser(GoogleAuthProvider.getCredential(account.idToken,null))
                .subscribe({
                    carDriver.postValue(it)
                },{
                    authError.postValue(it.message)
                })


    }

    @SuppressLint("CheckResult")
    fun signInUserWithFacebook(token:AccessToken){
            userRepository.signInUser(FacebookAuthProvider.getCredential(token.token))
                .subscribe({
                    carDriver.postValue(it)
                },{
                   Log.d(TAG,it.message)
                })
    }

    fun signOutUser(){
         userRepository.signOutUser()
    }

    fun isUserSignIn():Boolean{
        return userRepository.isUserSignIn()
    }

    @SuppressLint("CheckResult")
    fun setUserSubscriptionToModelState(state:Boolean, id:String){
       val carDriver =  carDriver.value ?: return
       userRepository.setUserSubscriptionState(carDriver.id,UserRepository.Channel.Model,id,state)
           .subscribe({
                if(state) carDriver.followedModels.add(id) else carDriver.followedModels.remove(id)
                this.carDriver.postValue(carDriver)
                subscriptionState.postValue(Event(Status.SUCCESS))
           },{
               Log.d(TAG,it.message)
               subscriptionState.postValue(Event(Status.FAILED))
           })
    }

    @SuppressLint("CheckResult")
    fun setUserSubscriptionToVersionState(state:Boolean, id:String){
        val carDriver =  carDriver.value ?: return
        userRepository.setUserSubscriptionState(carDriver.id,UserRepository.Channel.Version,id,state)
            .subscribe({
                if(state) carDriver.followedVersions.add(id) else carDriver.followedVersions.remove(id)
                this.carDriver.postValue(carDriver)
                subscriptionState.postValue(Event(Status.SUCCESS))
            },{
                Log.d(TAG,it.message)
                subscriptionState.postValue(Event(Status.FAILED))
            })
    }



    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}