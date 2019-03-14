package com.autolink.sayaradz.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.paging.PagedList
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.autolink.sayaradz.repository.utils.NetworkState
import com.autolink.sayaradz.ui.adapter.brand.BrandsAdapter
import com.autolink.sayaradz.viewmodel.BrandsViewModel
import com.autolink.sayaradz.vo.Brand
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.fragment_brands.*
import java.lang.Exception
import com.autolink.sayaradz.R
import androidx.recyclerview.widget.DividerItemDecoration
import com.autolink.sayaradz.util.RepositoryKey
import com.autolink.sayaradz.util.getViewModel
import com.autolink.sayaradz.viewmodel.UserViewModel
import kotlinx.android.synthetic.main.activity_main.*


class BrandsFragment: Fragment() {

    companion object {
        private const val TAG  = "BrandsFragment"
    }

    private val mBrandsViewModel by lazy {
        getViewModel(RepositoryKey.BRANDS_REPOSITORY) as BrandsViewModel
    }

    private val mBrandsAdapter by lazy {
        BrandsAdapter(Glide.with(context!!),context as BrandsAdapter.OnBrandsClickListener)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?)
            = inflater.inflate(com.autolink.sayaradz.R.layout.fragment_brands,container,false)!!



    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        brands_recycler_view.layoutManager =  LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        brands_recycler_view.adapter = mBrandsAdapter

        val dividerItemDecoration = DividerItemDecoration(brands_recycler_view.context, RecyclerView.VERTICAL)
        brands_recycler_view.addItemDecoration(dividerItemDecoration)


        mBrandsViewModel.brandsList.observe(viewLifecycleOwner, Observer{
            mBrandsAdapter.submitList(it)
        })


        initSwipeToRefresh()
        activity?.findViewById<TextView>(R.id.toolbar_title)?.text = resources.getString(R.string.app_name)


    }


    private fun initSwipeToRefresh() {
        mBrandsViewModel.refreshState.observe(this, Observer {
            brand_swipe_to_refresh_layout.isRefreshing = it == NetworkState.LOADING
        })

        brand_swipe_to_refresh_layout.setOnRefreshListener {
            mBrandsViewModel.refresh()
        }
    }

}
