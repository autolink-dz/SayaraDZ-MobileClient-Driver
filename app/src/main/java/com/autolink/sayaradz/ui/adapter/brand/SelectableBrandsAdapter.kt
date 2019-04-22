package com.autolink.sayaradz.ui.adapter.brand

import android.view.ViewGroup
import com.autolink.sayaradz.ui.adapter.BaseAdapter
import com.autolink.sayaradz.vo.Brand
import com.bumptech.glide.RequestManager

class SelectableBrandsAdapter(private val glide: RequestManager,
                              private val selectedBrandsList:MutableList<Brand>): BaseAdapter<Brand>(glide, BrandsAdapter.BRAND_COMPARATOR),BrandsAdapter.OnBrandsClickListener{


    companion object {
        private const val TAG  = "BrandsFragment"
        const val NON_SELECTED_VIEW_TYPE = 0
        const val SELECTED_VIEW_TYPE = 1
    }


    override fun getItemViewType(position: Int) =
        if(selectedBrandsList.contains(getItem(position))) SELECTED_VIEW_TYPE else NON_SELECTED_VIEW_TYPE


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        SelectableBrandViewHolder.create(parent,viewType,glide,this)


    override fun onBrandClick(brand: Brand) {
        val position  = currentList?.lastIndexOf(brand) ?: return
        val index  = selectedBrandsList.lastIndexOf(brand)
        if(index != -1 ) {
            selectedBrandsList.remove(brand)
        } else {
            selectedBrandsList.add(brand)
        }

        notifyItemChanged(position)

    }

    fun getSelectedBrands():List<Brand>{
        return this.selectedBrandsList
    }


}