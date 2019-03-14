package com.autolink.sayaradz.util

import android.content.Context
import android.preference.PreferenceManager
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.forEachIndexed
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.*
import com.autolink.sayaradz.repository.brand.BrandsRepository
import com.autolink.sayaradz.repository.models.ModelsRepository
import com.autolink.sayaradz.repository.user.UserRepository
import com.github.paolorotolo.appintro.AppIntro
import com.autolink.sayaradz.viewmodel.*
import com.google.android.gms.auth.api.credentials.IdToken
import com.google.firebase.auth.FirebaseAuth

inline fun < reified T>  ViewGroup.forEachViewOfType(action:(childView: View, index:Int)->Unit){

    this.forEachIndexed { index, view ->
           if(view is T) action(view,index)
    }

}


fun AppCompatActivity.isFirstRun(context:Context):Boolean{

     val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
     val result  =  sharedPreferences.getBoolean("FirstRun",true)

     if(result)  sharedPreferences.edit()
                        .putBoolean("FirstRun",false)
                        .apply()

     return  result

}

fun Context.writeToSharedPreference(key:String, value:String){
    val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)

    sharedPreferences.edit()
        .putString(key,value)
        .apply()
}

fun Context.readFromSharedPreference(key:String):String?{
    return PreferenceManager.getDefaultSharedPreferences(this)
        .getString(key,"")
}





fun Fragment.getViewModel(type: RepositoryKey): ViewModel {

    return ViewModelProviders.of(this, object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val repo = ServiceLocator.instance(activity!!)
                .getRepository(type)
            @Suppress("UNCHECKED_CAST")
            return when(type){
                RepositoryKey.USER_REPOSITORY -> UserViewModel(repo as UserRepository) as T
                RepositoryKey.BRANDS_REPOSITORY -> BrandsViewModel(repo as BrandsRepository) as T
                RepositoryKey.MODELS_REPOSITORY ->  ModelsViewModel(repo as ModelsRepository) as T
            }
        }
    })[when(type){
        RepositoryKey.USER_REPOSITORY ->UserViewModel::class.java
        RepositoryKey.BRANDS_REPOSITORY -> BrandsViewModel::class.java
        RepositoryKey.MODELS_REPOSITORY -> ModelsViewModel::class.java
    }]
}

fun AppCompatActivity.getViewModel(activity: FragmentActivity, type: RepositoryKey): ViewModel {

    return ViewModelProviders.of(this, object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val repo = ServiceLocator.instance(activity)
                .getRepository(type)
            @Suppress("UNCHECKED_CAST")
            return when(type){
                RepositoryKey.USER_REPOSITORY -> UserViewModel(repo as UserRepository) as T
                RepositoryKey.BRANDS_REPOSITORY -> BrandsViewModel(repo as BrandsRepository) as T
                RepositoryKey.MODELS_REPOSITORY ->  ModelsViewModel(repo as ModelsRepository) as T
            }
        }
    })[when(type){
        RepositoryKey.USER_REPOSITORY ->UserViewModel::class.java
        RepositoryKey.BRANDS_REPOSITORY -> BrandsViewModel::class.java
        RepositoryKey.MODELS_REPOSITORY -> ModelsViewModel::class.java
    }]

}