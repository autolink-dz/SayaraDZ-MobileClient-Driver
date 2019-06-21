package com.autolink.sayaradz.ui.fragment.newcar

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.invalidateOptionsMenu
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.autolink.sayaradz.R
import com.autolink.sayaradz.repository.utils.NetworkState
import com.autolink.sayaradz.repository.utils.Status
import com.autolink.sayaradz.ui.adapter.version.VersionsAdapter
import com.autolink.sayaradz.util.RepositoryKey
import com.autolink.sayaradz.util.getViewModel
import com.autolink.sayaradz.util.playAnimation
import com.autolink.sayaradz.viewmodel.UserViewModel
import com.autolink.sayaradz.viewmodel.VersionsViewModel
import com.autolink.sayaradz.vo.CarDriver
import com.autolink.sayaradz.vo.Model
import com.bumptech.glide.Glide
import com.google.android.material.animation.AnimationUtils
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_versions.*

class VersionsFragment:Fragment(){

    companion object {
        private const val TAG  = "VersionsFragment"
        const val MODEL_OBJECT_ARG_KEY = "model"
    }

    private val mVersionsViewModel by lazy {
        getViewModel(RepositoryKey.VERSIONS_REPOSITORY) as VersionsViewModel
    }

    private val mUserViewModel by lazy {
        activity?.run {
            ViewModelProviders.of(this).get(UserViewModel::class.java)
        } ?: throw Exception("Invalid Activity")
    }

    private val mVersionsAdapter by lazy {
        VersionsAdapter(Glide.with(context!!), context as VersionsAdapter.OnVersionClickListener,mModel)
    }

    private val mModel by lazy {
        arguments!![MODEL_OBJECT_ARG_KEY] as Model
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?)
        = inflater.inflate(R.layout.fragment_versions,container,false)

    @SuppressLint("StringFormatInvalid")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        versions_recycler_view.layoutManager =  LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        versions_recycler_view.adapter = mVersionsAdapter

        val dividerItemDecoration = DividerItemDecoration(versions_recycler_view.context, RecyclerView.VERTICAL)

        versions_recycler_view.addItemDecoration(dividerItemDecoration)

        mVersionsViewModel.setModel(mModel)

        mVersionsViewModel.versionList.observe(viewLifecycleOwner, Observer{

            mVersionsAdapter.submitList(it)
        })


        mUserViewModel.getCarDriverLiveData().observe(this, Observer {
            activity!!.invalidateOptionsMenu()
        })

        mUserViewModel.subscriptionStateLiveData.observe(this, Observer { status ->
            status.getContentIfNotHandled()?.let { // Only proceed if the event has never been handled //
                if(status.peekContent() == Status.FAILED) {

                    Snackbar.make(
                        view,
                        context!!.getString(R.string.subscription_failure_message, mModel.name),
                        Snackbar.LENGTH_LONG
                    )
                        .show()
                }

            }
        })
        initSwipeToRefresh()


        activity?.findViewById<TextView>(R.id.toolbar_title)?.text = mModel.name

    }

    private fun initSwipeToRefresh() {
        mVersionsViewModel.refreshState.observe(this, Observer {
            version_swipe_to_refresh_layout.isRefreshing = it == NetworkState.LOADING
            if(it == NetworkState.LOADING ){
                versions_shimmer_container.startShimmer()
                versions_shimmer_container.visibility = View.VISIBLE
            } else {
                versions_shimmer_container.stopShimmer()
                versions_shimmer_container.visibility = View.GONE
            }
        })

        version_swipe_to_refresh_layout.setOnRefreshListener {
            mVersionsViewModel.refresh()
        }
    }


    override fun onPrepareOptionsMenu(menu: Menu) {

        val carDriver = mUserViewModel.getCarDriverLiveData().value ?: return super.onPrepareOptionsMenu(menu)
        menu[1].isVisible = true

        if (carDriver.followedModels.indexOf(mModel.id) != -1)
             menu[1].icon = context!!.getDrawable(R.drawable.ic_notifications_active)
        else
             menu[1].icon = context!!.getDrawable(R.drawable.ic_notifications_none)

        super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == R.id.notification){
            val carDriver = mUserViewModel.getCarDriverLiveData().value ?: return  true

            if (carDriver.followedModels.indexOf(mModel.id) != -1)
                mUserViewModel.setUserSubscriptionToModelState(false,mModel.id)
            else
                mUserViewModel.setUserSubscriptionToModelState(true,mModel.id)
        }
        return true
    }




}