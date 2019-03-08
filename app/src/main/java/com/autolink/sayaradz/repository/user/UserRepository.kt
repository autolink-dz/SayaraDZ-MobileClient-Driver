package com.autolink.sayaradz.repository.user

import android.util.Log
import com.autolink.sayaradz.repository.IRepository
import com.autolink.sayaradz.vo.CarDriver
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import durdinapps.rxfirebase2.RxFirebaseAuth
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.Executor

class UserRepository(override val networkExecutor: Executor,
                     override val diskExecutor: Executor) : IRepository {
    companion object {
        private const val TAG = "UserRepository"
    }



    lateinit var compositeDisposable: CompositeDisposable


    init {

        RxFirebaseAuth.addIdTokenListener(FirebaseAuth.getInstance()) {auth ->
            auth.currentUser?.getIdToken(true)?.addOnCompleteListener {
                    Log.d(TAG,it.result?.token)
            }
        }
    }


    fun signInUser(credentials:AuthCredential):Maybe<CarDriver>{

        return RxFirebaseAuth.signInWithCredential(FirebaseAuth.getInstance(),credentials)
            .subscribeOn(Schedulers.from(networkExecutor))
            .observeOn(Schedulers.from(networkExecutor))
            .doOnSubscribe { compositeDisposable.add(it) }
            .map { it.user }
            .map { CarDriver(it.uid,it.displayName!!,it.email!!,it.photoUrl.toString()) }

    }

    fun getAuthUser():Observable<CarDriver>{
        return RxFirebaseAuth.observeAuthState(FirebaseAuth.getInstance())
                     .subscribeOn(Schedulers.from(networkExecutor))
                     .observeOn(Schedulers.from(networkExecutor))
                     .doOnSubscribe { compositeDisposable.add(it) }
                     .map { it.currentUser}
                     .switchMap {user->
                         Observable.create<CarDriver> {emitter->
                             user.getIdToken(true).addOnCompleteListener {
                                 emitter.onNext(CarDriver(user.uid,user.displayName!!,user.email!!,user.photoUrl.toString(),it.result?.token))
                                }
                            }
                     }


    }



    fun signOutUser(){
        FirebaseAuth.getInstance().signOut()
    }

    fun isUserSignIn():Boolean{
        return FirebaseAuth.getInstance().currentUser != null
    }
}