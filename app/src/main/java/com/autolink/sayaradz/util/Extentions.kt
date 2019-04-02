package com.autolink.sayaradz.util

import android.content.Context
import android.preference.PreferenceManager
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.Interpolator
import androidx.annotation.AnimRes
import androidx.annotation.InterpolatorRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.forEachIndexed
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.*
import androidx.vectordrawable.graphics.drawable.AnimationUtilsCompat
import com.autolink.sayaradz.repository.announcement.AnnouncementsRepository
import com.autolink.sayaradz.repository.brand.BrandsRepository
import com.autolink.sayaradz.repository.model.ModelsRepository
import com.autolink.sayaradz.repository.tariff.TariffRepository
import com.autolink.sayaradz.repository.user.UserRepository
import com.autolink.sayaradz.repository.version.VersionsRepository
import com.autolink.sayaradz.util.RepositoryKey.*
import com.autolink.sayaradz.viewmodel.*
import com.bumptech.glide.Glide


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
                USER_REPOSITORY -> UserViewModel(repo as UserRepository) as T
                BRANDS_REPOSITORY -> BrandsViewModel(repo as BrandsRepository) as T
                MODELS_REPOSITORY ->  ModelsViewModel(repo as ModelsRepository) as T
                VERSIONS_REPOSITORY ->  VersionsViewModel(repo as VersionsRepository) as T
                TARIFF_REPOSITORY -> TariffViewModel(repo as TariffRepository) as T
                ANNOUNCEMENT_REPOSITORY -> AnnouncementsViewModel(repo as AnnouncementsRepository) as T
            }
        }
    })[when(type){
        USER_REPOSITORY ->UserViewModel::class.java
        BRANDS_REPOSITORY -> BrandsViewModel::class.java
        MODELS_REPOSITORY -> ModelsViewModel::class.java
        VERSIONS_REPOSITORY -> VersionsViewModel::class.java
        TARIFF_REPOSITORY -> TariffViewModel::class.java
        ANNOUNCEMENT_REPOSITORY -> AnnouncementsViewModel::class.java
    }]
}

fun AppCompatActivity.getViewModel(activity: FragmentActivity, type: RepositoryKey): ViewModel {

    return ViewModelProviders.of(this, object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val repo = ServiceLocator.instance(activity)
                .getRepository(type)
            @Suppress("UNCHECKED_CAST")
            return when(type){
                USER_REPOSITORY -> UserViewModel(repo as UserRepository) as T
                BRANDS_REPOSITORY -> BrandsViewModel(repo as BrandsRepository) as T
                MODELS_REPOSITORY ->  ModelsViewModel(repo as ModelsRepository) as T
                VERSIONS_REPOSITORY ->  VersionsViewModel(repo as VersionsRepository) as T
                TARIFF_REPOSITORY -> TariffViewModel(repo as TariffRepository) as T
                ANNOUNCEMENT_REPOSITORY -> AnnouncementsViewModel(repo as AnnouncementsRepository) as T
            }
        }
    })[when(type){
        USER_REPOSITORY ->UserViewModel::class.java
        BRANDS_REPOSITORY -> BrandsViewModel::class.java
        MODELS_REPOSITORY -> ModelsViewModel::class.java
        VERSIONS_REPOSITORY -> VersionsViewModel::class.java
        TARIFF_REPOSITORY -> TariffViewModel::class.java
        ANNOUNCEMENT_REPOSITORY -> AnnouncementsViewModel::class.java
    }]

}

private class AnimationListener(
    private val onAnimationRepeat: () -> Unit,
    private val onAnimationStart: () -> Unit,
    private val onAnimationEnd: () -> Unit
) : Animation.AnimationListener {
    override fun onAnimationRepeat(p0: Animation?) = onAnimationRepeat()
    override fun onAnimationStart(p0: Animation?) = onAnimationStart()
    override fun onAnimationEnd(p0: Animation?) = onAnimationEnd()
}

fun View.playAnimation(
    @AnimRes animResId: Int,
    @InterpolatorRes interpolator:Int,
    onAnimationRepeat: () -> Unit = {},
    onAnimationStart: () -> Unit = {},
    onAnimationEnd: () -> Unit = {}
) = with(AnimationUtils.loadAnimation(context, animResId)) {
    setAnimationListener(AnimationListener(onAnimationRepeat, onAnimationStart, onAnimationEnd))
    startAnimation(this)
    setInterpolator(context,interpolator)
}



fun dp2px(context: Context, dp: Float): Int {
    val scale = context.getResources().getDisplayMetrics().density
    return (dp * scale + 0.5f).toInt()
}
