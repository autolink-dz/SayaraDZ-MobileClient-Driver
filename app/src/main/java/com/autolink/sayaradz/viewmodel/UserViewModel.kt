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

class UserViewModel(val userRepository: UserRepository): ViewModel() {

    companion object {
        private const val TAG = "UserViewModel"
        private const val GOOGLE_PROVIDER_ID = "google.com"
    }

    private val carDriver:MutableLiveData<CarDriver>  = MutableLiveData()

    private val authError:MutableLiveData<String> = MutableLiveData()


    fun getCarDriverLiveData():LiveData<CarDriver> = carDriver

    fun getAuthErrorLiveData():LiveData<String> = authError

    @SuppressLint("CheckResult")
    fun signInUserWithGoogle(account:GoogleSignInAccount){

            userRepository.signInUser(GoogleAuthProvider.getCredential(account.idToken,null))
                .subscribe({
                    carDriver.value = it
                },{
                    authError.value = it.message
                })


    }

    @SuppressLint("CheckResult")
    fun signInUserWithFacebook(token:AccessToken){
            userRepository.signInUser(FacebookAuthProvider.getCredential(token.token))
                .subscribe({
                    carDriver.value = it
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
}