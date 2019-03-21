package com.autolink.sayaradz.ui.fragment.newcar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.autolink.sayaradz.R
import com.autolink.sayaradz.repository.utils.NetworkState
import com.autolink.sayaradz.ui.adapter.model.ModelsAdapter
import com.autolink.sayaradz.util.RepositoryKey
import com.autolink.sayaradz.util.getViewModel
import com.autolink.sayaradz.viewmodel.ModelsViewModel
import com.autolink.sayaradz.vo.Brand
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.fragment_models.*

class ModelsFragment: Fragment(){

    companion object {
        private const val TAG  = "ModelsFragment"
    }

    private val mModelsViewModel by lazy {
        getViewModel(RepositoryKey.MODELS_REPOSITORY) as ModelsViewModel
    }

    private val mModelsAdapter by lazy {
        ModelsAdapter(Glide.with(context!!),context as ModelsAdapter.OnModelClickListener)
    }

    private val mBrand by lazy {
        arguments!![BrandsFragment.BRAND_OBJECT_ARG_KEY] as Brand
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?)
            = inflater.inflate(R.layout.fragment_models,container,false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        models_recycler_view.layoutManager =  LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        models_recycler_view.adapter = mModelsAdapter

        val dividerItemDecoration = DividerItemDecoration(models_recycler_view.context, RecyclerView.VERTICAL)
        models_recycler_view.addItemDecoration(dividerItemDecoration)

        mModelsViewModel.setBrandId(mBrand.id)

        mModelsViewModel.modelsList.observe(viewLifecycleOwner, Observer{
           mModelsAdapter.submitList(it)
        })


        initSwipeToRefresh()
        activity?.findViewById<TextView>(R.id.toolbar_title)?.text = mBrand.name


    }


    private fun initSwipeToRefresh() {
        mModelsViewModel.refreshState.observe(this, Observer {
            model_swipe_to_refresh_layout.isRefreshing = it == NetworkState.LOADING
        })

        model_swipe_to_refresh_layout.setOnRefreshListener {
            mModelsViewModel.refresh()
        }
    }

}
