package com.autolink.sayaradz.viewmodel

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.autolink.sayaradz.repository.user.UserRepository
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

    private val compositeDisposable = CompositeDisposable()


    init {
        userRepository.compositeDisposable = compositeDisposable
        userRepository.getAuthUser()
            .subscribe({
                carDriver.postValue(it)
            },{
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

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}