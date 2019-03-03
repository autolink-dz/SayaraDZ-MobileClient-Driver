package com.autolink.sayaradz.repository.user

import com.autolink.sayaradz.repository.IRepository
import com.autolink.sayaradz.vo.CarDriver
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import durdinapps.rxfirebase2.RxFirebaseAuth
import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.Executor

class UserRepository(override val networkExecutor: Executor,
                     override val diskExecutor: Executor) : IRepository {
    companion object {
        private const val TAG = "UserRepository"
    }


    fun signInUser(credentials:AuthCredential):Maybe<CarDriver>{

        return RxFirebaseAuth.signInWithCredential(FirebaseAuth.getInstance(),credentials)
            .subscribeOn(Schedulers.from(networkExecutor))
            .map { it.user }
            .map { CarDriver(it.uid,it.displayName!!,it.email!!,it.photoUrl.toString()) }

    }

    fun getAuthUser():Single<CarDriver>{
        return Single.just(FirebaseAuth.getInstance().currentUser)
                     .subscribeOn(Schedulers.from(diskExecutor))
                     .map { CarDriver(it.uid,it.displayName!!,it.email!!,it.photoUrl.toString()) }
    }


    fun signOutUser(){
        FirebaseAuth.getInstance().signOut()
    }

    fun isUserSignIn():Boolean{
        return FirebaseAuth.getInstance().currentUser != null
    }
}