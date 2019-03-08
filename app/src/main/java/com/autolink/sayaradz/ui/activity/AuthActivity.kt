package com.autolink.sayaradz.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.Observer
import com.autolink.sayaradz.R
import com.autolink.sayaradz.ui.fragment.AuthFragment
import com.autolink.sayaradz.ui.fragment.IntroFragment
import com.autolink.sayaradz.util.*
import com.autolink.sayaradz.viewmodel.UserViewModel
import com.autolink.sayaradz.vo.CarDriver
import com.facebook.AccessToken
import com.github.paolorotolo.appintro.AppIntro
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException

class AuthActivity: AppIntro(),AuthFragment.OnGoogleSignIn,AuthFragment.OnFacebookSignIn {


    companion object {
        private const val INTRO_SLIDE_NUMBER = 4
        private const val RC_SIGN_IN  = 1
        private const val TAG  = "AuthActivity"
    }


    private lateinit var mUserViewModel:UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (isFirstRun(this)) setUpIntroPosters()

        addSlide(AuthFragment.newInstance(R.layout.fragment_auth))
        setUpIntroActivityLayout()


        mUserViewModel = getViewModel(this,RepositoryKey.USER_REPOSITORY) as UserViewModel



        mUserViewModel.getCarDriverLiveData().observe(this, Observer<CarDriver> {
              val intent = Intent(this,MainActivity::class.java)
              intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP shl Intent.FLAG_ACTIVITY_NEW_TASK)
              startActivity(intent)
              finish()
        })

        mUserViewModel.getAuthErrorLiveData().observe(this,Observer<String>{
            Log.d(TAG,it)
        })


    }

    private fun setUpIntroActivityLayout(){

        showSkipButton(false)
        showStatusBar(false)
        setIndicatorColor(resources.getColor(R.color.colorPrimary),resources.getColor(R.color.grey))
        setNextArrowColor(resources.getColor(R.color.colorPrimary))
        showSeparator(false)

        setCustomTransformer(CostumePageTransformer())

        getPager().addOnPageChangeListener(object : PagerOnPageChangeListener(){

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                isProgressButtonEnabled = position != (INTRO_SLIDE_NUMBER-1)
            }
        })
    }

    private fun setUpIntroPosters(){
        addSlide(IntroFragment.newInstance(R.layout.fragment_intro_slide_1))
        addSlide(IntroFragment.newInstance(R.layout.fragment_intro_slide_2))
        addSlide(IntroFragment.newInstance(R.layout.fragment_intro_slide_3))
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)

            if (result.isSuccess){
                val account = task.getResult(ApiException::class.java)!!
                Log.d(TAG, "Google sign in Succeeded")
                mUserViewModel.signInUserWithGoogle(account)
            }else{
                Log.w(TAG, "Google sign in failed "+result.status)
            }

        }
    }


    override fun onGoogleSignIn() {

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.web_client_id))
            .requestEmail()
            .build()

        val googleSignInClient = GoogleSignIn.getClient(this,gso)
        startActivityForResult(googleSignInClient.signInIntent, RC_SIGN_IN)

    }

    override fun onFacebookSignIn(token: AccessToken) {
        mUserViewModel.signInUserWithFacebook(token)
    }





}