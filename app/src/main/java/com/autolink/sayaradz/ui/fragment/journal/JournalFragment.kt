package com.autolink.sayaradz.ui.fragment.journal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.autolink.sayaradz.R
import com.autolink.sayaradz.viewmodel.UserViewModel
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.fragment_journal.*

class JournalFragment: Fragment() {



    private val mUserViewModel by lazy {
        activity?.run {
            ViewModelProviders.of(this).get(UserViewModel::class.java)
        } ?: throw Exception("Invalid Activity")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?)
            = inflater.inflate(R.layout.fragment_journal,container,false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity?.findViewById<TextView>(R.id.toolbar_title)?.text = resources.getString(R.string.app_name)
        mUserViewModel.getCarDriverLiveData().observe(this, Observer {

            Glide.with(context!!)
                .load(it.photoURL)
                .into(user_image)

            username_tv.text = it.name
            email_tv.text = it.email
        })
    }


}