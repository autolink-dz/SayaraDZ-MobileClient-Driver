package com.autolink.sayaradz.ui.fragment.oldcar

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.transition.Slide
import androidx.transition.TransitionInflater
import com.autolink.sayaradz.R
import com.autolink.sayaradz.ui.fragment.RoundedBottomSheetDialogFragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import android.view.MotionEvent
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.autolink.sayaradz.ui.adapter.brand.BrandsAdapter
import com.autolink.sayaradz.ui.adapter.brand.SelectableBrandsAdapter
import com.autolink.sayaradz.viewmodel.BrandsViewModel
import com.autolink.sayaradz.vo.Brand
import com.bumptech.glide.Glide
import com.jaygoo.widget.OnRangeChangedListener
import com.jaygoo.widget.RangeSeekBar
import kotlinx.android.synthetic.main.fragment_brands.*
import kotlinx.android.synthetic.main.fragment_filter_announcements.*
import java.lang.Float.POSITIVE_INFINITY
import kotlin.math.max
import kotlin.math.min


class AnnouncementsFilterSheetFragment:RoundedBottomSheetDialogFragment(){


    companion object {
        const val MIN_PRICE_KEY = "MIN_PRICE_KEY"
        const val MAX_PRICE_KEY = "MAX_PRICE_KEY"
        const val MIN_DISTANCE_KEY = "MIN_DISTANCE_KEY"
        const val MAX_DISTANCE_KEY = "MAX_DISTANCE_KEY"
        const val BRANDS_KEY = "BRANDS_KEY"
    }

    private val mBrandsViewModel by lazy {
        activity?.run {
            ViewModelProviders.of(this).get(BrandsViewModel::class.java)
        } ?: throw Exception("Invalid Activity")
    }



    private lateinit var  mBrandsAdapter:SelectableBrandsAdapter

    private var minPriceValue:Float = 0.0f
    private var maxPriceValue:Float = 0.0f

    private var minDistanceValue:Float = 0.0f
    private var maxDistanceValue:Float = 0.0f

    interface OnFilterSubmittedListener{
        fun onFilterSubmitted(brands:List<Brand>,priceRange: Pair<Float,Float>, distanceRange: Pair<Float,Float>)
        fun onFilterCleared()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val d = super.onCreateDialog(savedInstanceState)
        //view hierarchy is inflated after dialog is shown
        d.setOnShowListener {
            //this prevents dragging behavior
            val layout = d.window.findViewById<View>(R.id.design_bottom_sheet)
            BottomSheetBehavior.from(layout).state = BottomSheetBehavior.STATE_EXPANDED

        }
        return d
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_filter_announcements,container,false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val brands = arguments?.getParcelableArrayList<Brand>(BRANDS_KEY)?.toMutableList() ?: mutableListOf<Brand>()

        minPriceValue = arguments?.getFloat(MIN_PRICE_KEY) ?: 0F
        maxPriceValue = arguments?.getFloat(MAX_PRICE_KEY) ?: 0F

        price_range_seek_bar.setValue(minPriceValue, maxPriceValue)
        price_range_seek_bar.setIndicatorTextDecimalFormat("0.0 millions")
        price_range_seek_bar.setOnRangeChangedListener(object : OnRangeChangedListener{
            override fun onStartTrackingTouch(view: RangeSeekBar?, isLeft: Boolean) {}
            override fun onRangeChanged(view: RangeSeekBar?, leftValue: Float, rightValue: Float, isFromUser: Boolean) {
                minPriceValue = min(leftValue,rightValue)
                maxPriceValue = max(leftValue,rightValue)
            }
            override fun onStopTrackingTouch(view: RangeSeekBar?, isLeft: Boolean) {}
        })


        minDistanceValue = arguments?.getFloat(MIN_DISTANCE_KEY) ?: 0F
        maxDistanceValue = arguments?.getFloat(MAX_DISTANCE_KEY) ?: 0F


        distance_range_seek_bar.setValue(minDistanceValue,maxDistanceValue)
        distance_range_seek_bar.setIndicatorTextDecimalFormat("0 milles km")
        distance_range_seek_bar.setOnRangeChangedListener(object : OnRangeChangedListener{
            override fun onStartTrackingTouch(view: RangeSeekBar?, isLeft: Boolean) {}

            override fun onRangeChanged(view: RangeSeekBar?, leftValue: Float, rightValue: Float, isFromUser: Boolean) {
                minDistanceValue = min(leftValue,rightValue)
                maxDistanceValue = max(leftValue,rightValue)
            }

            override fun onStopTrackingTouch(view: RangeSeekBar?, isLeft: Boolean) {}
        })

        mBrandsAdapter = SelectableBrandsAdapter(Glide.with(context!!), brands)

        brand_filter_recycler_view.layoutManager =  LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        brand_filter_recycler_view.adapter = mBrandsAdapter

        mBrandsViewModel.brandsList.observe(viewLifecycleOwner, Observer{
            mBrandsAdapter.submitList(it)
        })





        cancel_filter_dialog_button.setOnClickListener {

            this.dismiss()
            this.onDestroy()
        }
        reset_filter_button.setOnClickListener {

            (context as OnFilterSubmittedListener).onFilterCleared()
            this.dismiss()
        }
        submit_filter_button.setOnClickListener {

            val list = mBrandsAdapter.getSelectedBrands()

            (context as OnFilterSubmittedListener).onFilterSubmitted(list,Pair(minPriceValue,maxPriceValue),Pair(minDistanceValue,maxDistanceValue))
            this.dismiss()
            this.onDestroy()
        }
    }


}
