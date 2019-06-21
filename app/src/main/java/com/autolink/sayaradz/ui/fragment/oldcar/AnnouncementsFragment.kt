package com.autolink.sayaradz.ui.fragment.oldcar

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.get
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.autolink.sayaradz.R
import com.autolink.sayaradz.repository.utils.NetworkState
import com.autolink.sayaradz.ui.adapter.announcement.AnnouncementsAdapter
import com.autolink.sayaradz.ui.adapter.brand.BrandsAdapter
import com.autolink.sayaradz.util.GlideApp
import com.autolink.sayaradz.util.OnScrollStateChangedListener
import com.autolink.sayaradz.util.RepositoryKey
import com.autolink.sayaradz.util.getViewModel
import com.autolink.sayaradz.viewmodel.AnnouncementsViewModel
import com.autolink.sayaradz.viewmodel.UserViewModel
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_announcements.*
import kotlinx.android.synthetic.main.fragment_brands.*


class AnnouncementsFragment : Fragment() {


    private val mAnnouncementsViewModel by lazy {
        activity?.run {
            ViewModelProviders.of(this).get(AnnouncementsViewModel::class.java)
        } ?: throw Exception("Invalid Activity")
    }

    private val mAnnouncementsAdapter by lazy {
        AnnouncementsAdapter(Glide.with(context!!),context as AnnouncementsAdapter.OnAnnouncementClickListener)
    }

    companion object {
        const val TAG = "AnnouncementsFragment"
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?)
            = inflater.inflate(R.layout.fragment_announcements,container,false)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        announcements_recycler_view.addOnScrollListener(object : RecyclerView.OnScrollListener(){

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                when (newState) {
                    RecyclerView.SCROLL_STATE_IDLE ->      (context as OnScrollStateChangedListener).onScrollStateChanged(
                        scrolling = false,
                        up = true
                    )
                    else -> {}
                }
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                (context as OnScrollStateChangedListener).onScrollStateChanged(dy != 0,dy <= 0)
            }
        })

        announcements_recycler_view.layoutManager =  LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        announcements_recycler_view.adapter = mAnnouncementsAdapter

        mAnnouncementsViewModel.announcementsList.observe(this, Observer {
            mAnnouncementsAdapter.submitList(it)
        })

        initSwipeToRefresh()
        activity?.findViewById<TextView>(R.id.toolbar_title)?.text = resources.getString(R.string.app_name)
    }


    private fun initSwipeToRefresh() {
        mAnnouncementsViewModel.refreshState.observe(this, Observer {
            announcements_swipe_to_refresh.isRefreshing = it == NetworkState.LOADING
            if(it == NetworkState.LOADING ){
                annoucements_shimmer_container.startShimmer()
                annoucements_shimmer_container.visibility = View.VISIBLE
            } else {
                annoucements_shimmer_container.stopShimmer()
                annoucements_shimmer_container.visibility = View.GONE
            }
        })

        announcements_swipe_to_refresh.setOnRefreshListener {
            mAnnouncementsViewModel.refresh()
        }
    }


}
