package com.autolink.sayaradz.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.autolink.sayaradz.R
import com.autolink.sayaradz.ui.fragment.BrandsFragment
import com.autolink.sayaradz.util.RepositoryKey
import com.autolink.sayaradz.util.getViewModel
import com.autolink.sayaradz.util.writeToSharedPreference
import com.autolink.sayaradz.viewmodel.BrandsViewModel
import com.autolink.sayaradz.viewmodel.UserViewModel

class MainActivity:AppCompatActivity(){

    companion object {
        private const val TAG  = "MainActivity"
    }

    private lateinit var mUserViewModel: UserViewModel
    private lateinit var mBrandsViewModel: BrandsViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        mUserViewModel = getViewModel(this, RepositoryKey.USER_REPOSITORY) as UserViewModel
        mBrandsViewModel = getViewModel(this, RepositoryKey.BRANDS_REPOSITORY) as BrandsViewModel


        if(!mUserViewModel.isUserSignIn()){
            val intent = Intent(this,AuthActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP shl Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }

        mUserViewModel.getCarDriverLiveData().observe(this, Observer {
            Log.d(TAG,"updating the user token")
            it.token?.let { it1 -> writeToSharedPreference("TOKEN", it1) }
        })

        setUpToolBar()

        supportFragmentManager.beginTransaction()
            .add(R.id.fragment_container,BrandsFragment())
            .commit()

    }




    private fun setUpToolBar(){
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.title = ""
    }
}