package com.autolink.sayaradz.ui.fragment.oldcar

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders


import com.autolink.sayaradz.R
import com.autolink.sayaradz.viewmodel.AnnouncementsViewModel
import com.autolink.sayaradz.vo.Brand
import com.autolink.sayaradz.vo.Version
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.fragment_new_announcement.*


class NewAnnouncementFragment : Fragment(){


    private lateinit var mBinding: com.autolink.sayaradz.databinding.FragmentNewAnnouncementBinding

    private val mAnnouncementsViewModel by lazy {
        activity?.run {
            ViewModelProviders.of(this).get(AnnouncementsViewModel::class.java)
        } ?: throw Exception("Invalid Activity")
    }

    interface OnSelectVehicleClickListener{
        fun onSelectVehicleClicked()
    }
    interface OnSelectPhotoClickListener{
        fun onSelectPhotoClicked()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?):View {
        mBinding  = DataBindingUtil.inflate(inflater,R.layout.fragment_new_announcement,container,false)
        return mBinding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        mBinding.setLifecycleOwner(this)
        mBinding.viewmodel = mAnnouncementsViewModel


        select_car_layout.setOnClickListener {
            (context as OnSelectVehicleClickListener).onSelectVehicleClicked()
        }

        select_photo_layout.setOnClickListener {
            (context as OnSelectPhotoClickListener).onSelectPhotoClicked()
        }
    }

    fun setVehicleProviderCredential(brand:Brand,version:Version){


            car_brand_image_view.visibility = View.VISIBLE
            Glide.with(context!!)
                .load(brand.photoURL)
                .into(car_brand_image_view)

        car_version_image_view.visibility = View.VISIBLE
        Glide.with(context!!)
            .load(version.photoURL)
            .into(car_version_image_view)

        car_name_text_view.text = version.name

    }

    fun setVehiclePhoto(url:String){


        vehicle_image_view.visibility = View.VISIBLE
        Glide.with(context!!)
            .load(url)
            .into(vehicle_image_view)
    }


}
