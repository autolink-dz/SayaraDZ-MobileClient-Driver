package com.autolink.sayaradz.repository.user

import android.content.Context
import android.util.Log
import com.autolink.sayaradz.repository.IRepository
import com.autolink.sayaradz.util.writeToSharedPreference
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
import javax.security.auth.Subject

class UserRepository(val context: Context,
                     override val networkExecutor: Executor,
                     override val diskExecutor: Executor) : IRepository {
    companion object {
        private const val TAG = "UserRepository"
        const val USER_TOKEN_KEY  = "TOKEN"
    }


    lateinit var compositeDisposable: CompositeDisposable
    private val tokenListener = FirebaseAuth.IdTokenListener {auth->
        Log.d(TAG,"token is updated")
        auth.currentUser?.getIdToken(false)?.addOnCompleteListener {
            Log.d(TAG,"updating the token in the shared preferences")
            context.writeToSharedPreference(USER_TOKEN_KEY,it.result?.token.toString())
        }
    }

    init {

        FirebaseAuth.getInstance().addIdTokenListener(tokenListener)

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
                     .map {
                            CarDriver(it.uid,it.displayName!!,it.email!!,it.photoUrl.toString())
                     }

    }



    fun signOutUser(){
        FirebaseAuth.getInstance().signOut()
    }

    fun isUserSignIn():Boolean{
        return FirebaseAuth.getInstance().currentUser != null
    }

    override fun clear() {
        FirebaseAuth.getInstance().removeIdTokenListener(tokenListener)
    }
}