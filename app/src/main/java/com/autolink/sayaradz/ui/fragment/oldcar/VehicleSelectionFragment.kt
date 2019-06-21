package com.autolink.sayaradz.ui.fragment.oldcar

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.autolink.sayaradz.R
import com.autolink.sayaradz.repository.utils.NetworkState
import com.autolink.sayaradz.ui.adapter.brand.BrandsAdapter
import com.autolink.sayaradz.ui.adapter.model.ModelsAdapter
import com.autolink.sayaradz.ui.adapter.version.VersionsAdapter
import com.autolink.sayaradz.ui.fragment.RoundedBottomSheetDialogFragment
import com.autolink.sayaradz.util.RepositoryKey
import com.autolink.sayaradz.util.getViewModel
import com.autolink.sayaradz.viewmodel.BrandsViewModel
import com.autolink.sayaradz.viewmodel.ModelsViewModel
import com.autolink.sayaradz.viewmodel.VersionsViewModel
import com.autolink.sayaradz.vo.Brand
import com.autolink.sayaradz.vo.Model
import com.autolink.sayaradz.vo.Version
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.yarolegovich.discretescrollview.transform.Pivot
import com.yarolegovich.discretescrollview.transform.ScaleTransformer
import kotlinx.android.synthetic.main.fragment_car_selection.*
import kotlinx.android.synthetic.main.fragment_filter_announcements.*

class VehicleSelectionFragment:RoundedBottomSheetDialogFragment(),
    BrandsAdapter.OnBrandsClickListener,
    ModelsAdapter.OnModelClickListener,
    VersionsAdapter.OnVersionClickListener{

    private lateinit var mSelectedBrand: Brand
    interface OnVehicleSelectionCompletedListener{
        fun  onVehicleSelectionCompleted(version: Version,brand:Brand)
    }


    private val mBrandsViewModel by lazy {
        activity?.run {
            ViewModelProviders.of(this).get(BrandsViewModel::class.java)
        } ?: throw Exception("Invalid Activity")
    }
    private val mVersionsViewModel by lazy {
        getViewModel(RepositoryKey.VERSIONS_REPOSITORY) as VersionsViewModel
    }
    private val mModelsViewModel by lazy {
        getViewModel(RepositoryKey.MODELS_REPOSITORY) as ModelsViewModel
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val d = super.onCreateDialog(savedInstanceState)
        d.setOnShowListener {
            val layout = d.window.findViewById<View>(R.id.design_bottom_sheet)
            BottomSheetBehavior.from(layout).state = BottomSheetBehavior.STATE_EXPANDED
        }
        return d
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?)
      = inflater.inflate(R.layout.fragment_car_selection,container,false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val brandsAdapter =  BrandsAdapter(Glide.with(context!!),this,BrandsAdapter.CARD_LIST_KEY)
        with(vehicle_search_recycler_view){
            adapter = brandsAdapter
            setItemTransformer(
                ScaleTransformer.Builder()
                    .setMaxScale(1.05f)
                    .setMinScale(0.8f)
                    .setPivotX(Pivot.X.CENTER)
                    .setPivotY(Pivot.Y.BOTTOM)
                    .build())
            setSlideOnFling(true)
            addOnItemChangedListener { viewHolder, i ->
                val tag = viewHolder?.itemView?.tag
                when(tag){
                    is Brand -> image_card_label_text_view.text = tag.name
                    is Model -> image_card_label_text_view.text = tag.name
                    is Version -> image_card_label_text_view.text = tag.name
                    else ->  image_card_label_text_view.text = String()
                }
            }
        }




        mBrandsViewModel.brandsList.observe(viewLifecycleOwner, Observer{
            brandsAdapter.submitList(it)
        })

    }

    override fun onBrandClick(brand: Brand) {


        mSelectedBrand = brand
        mModelsViewModel.setBrandId(brand.id)

        val modelsAdapter =  ModelsAdapter(Glide.with(context!!),this,ModelsAdapter.CARD_LIST_KEY)
        vehicle_search_recycler_view.adapter = modelsAdapter



        mModelsViewModel.modelsList.observe(viewLifecycleOwner, Observer{
            modelsAdapter.submitList(it)

        })


        vehicle_search_title.text = context!!.resources.getString(R.string.choose_model_title)
        vehicle_search_hint.text = brand.name
        vehicle_search_hint.visibility = View.VISIBLE

    }
    override fun onModelClick(model: Model) {

        mVersionsViewModel.setModel(model)

        val versionAdapter =  VersionsAdapter(Glide.with(context!!),this,model,VersionsAdapter.CARD_LIST_KEY)
        vehicle_search_recycler_view.adapter = versionAdapter



        mVersionsViewModel.versionList.observe(viewLifecycleOwner, Observer{
            versionAdapter.submitList(it)
        })


        vehicle_search_title.text = context!!.resources.getString(R.string.choose_version_title)
        vehicle_search_hint.text = model.name


    }
    override fun onVersionClick(version: Version, imageView: ImageView) {
        (context as OnVehicleSelectionCompletedListener).onVehicleSelectionCompleted(version,mSelectedBrand)
        this.dismiss()
        this.onDestroy()
    }


}