package com.autolink.sayaradz.repository.user

import android.annotation.SuppressLint
import android.util.Log
import com.autolink.sayaradz.api.SayaraDzApi
import com.autolink.sayaradz.repository.IRepository
import com.autolink.sayaradz.vo.CarDriver
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import durdinapps.rxfirebase2.RxFirebaseAuth
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.Executor

class UserRepository(val api: SayaraDzApi,
                     override val networkExecutor: Executor,
                     override val diskExecutor: Executor) : IRepository {
    companion object {
        private const val TAG = "UserRepository"
    }

    enum class Channel(val value: String) {
        Model("modeles"),
        Version("versions"),

    }


    lateinit var compositeDisposable: CompositeDisposable



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
                     .map { it.currentUser?.uid }
                     .flatMap {
                         Log.d(TAG,"sending the request to get the user with uid $it")
                         api.getCarDriver(it)}

    }

    fun signOutUser(){
        FirebaseAuth.getInstance().signOut()
    }

    fun isUserSignIn():Boolean{
        return FirebaseAuth.getInstance().currentUser != null
    }

    @SuppressLint("CheckResult")
    fun setUserSubscriptionState(uid:String, itemType:Channel, itemId:String, state:Boolean):Observable<Any>{
        val request  = if(state) api.follow(itemType.value,itemId,uid) else api.unfollow(itemType.value,itemId,uid)
        return request.subscribeOn(Schedulers.from(networkExecutor))
               .observeOn(Schedulers.from(networkExecutor))
               .doOnSubscribe { compositeDisposable.add(it) }
    }

    override fun clear() {

    }
}