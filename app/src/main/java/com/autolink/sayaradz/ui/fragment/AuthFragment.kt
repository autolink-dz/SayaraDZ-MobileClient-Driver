package com.autolink.sayaradz.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.autolink.sayaradz.ui.activity.AuthActivity
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import kotlinx.android.synthetic.main.fragment_auth.*

class AuthFragment: Fragment() {

    private var layoutResId: Int = 0
    private lateinit var mCallbackManager: CallbackManager

    companion object {
        private const val TAG  = "AuthFragment"
        private const val ARG_LAYOUT_RES_ID = "layoutResId"
        private const val EMAIL = "email"
        private const val PUBLIC_PROFILE = "public_profile"

        fun newInstance(layoutResId: Int): AuthFragment {
            val poster = AuthFragment()

            val args = Bundle()
            args.putInt(ARG_LAYOUT_RES_ID, layoutResId)
            poster.arguments = args

            return poster
        }
    }

    interface OnGoogleSignIn{
        fun onGoogleSignIn()
    }

    interface OnFacebookSignIn{
        fun onFacebookSignIn(token:AccessToken)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (arguments?.containsKey(ARG_LAYOUT_RES_ID)!!) {
            layoutResId = arguments!!.getInt(ARG_LAYOUT_RES_ID)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(layoutResId, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        google_sign_in_button.setOnClickListener {
            Log.d(TAG,"google_sign_in_button.setOnClickListener")
            (context as OnGoogleSignIn).onGoogleSignIn()
        }

        facebook_sign_in_button.setReadPermissions(EMAIL,PUBLIC_PROFILE)
        facebook_sign_in_button.fragment = this

        mCallbackManager = CallbackManager.Factory.create()
        LoginManager.getInstance().registerCallback(mCallbackManager,object : FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult?) {
                if( result != null) (activity as OnFacebookSignIn).onFacebookSignIn(result.accessToken)
            }

            override fun onCancel() {}

            override fun onError(error: FacebookException?) {

            }

        })

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        mCallbackManager.onActivityResult(requestCode,resultCode,data)
        super.onActivityResult(requestCode, resultCode, data)
    }
}