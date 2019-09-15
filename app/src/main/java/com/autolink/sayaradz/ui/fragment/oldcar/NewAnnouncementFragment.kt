package com.autolink.sayaradz.ui.fragment.oldcar

import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders


import com.autolink.sayaradz.R
import com.autolink.sayaradz.repository.utils.Status
import com.autolink.sayaradz.viewmodel.AnnouncementsViewModel
import com.autolink.sayaradz.vo.Brand
import com.autolink.sayaradz.vo.Version
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
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

    interface OnAnnouncementSubmittedListener{
        fun onAnnouncementSubmitted()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?):View {
        mBinding  = DataBindingUtil.inflate(inflater,R.layout.fragment_new_announcement,container,false)
        return mBinding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        mBinding.setLifecycleOwner(this)
        mBinding.announcementviewmodel = mAnnouncementsViewModel


        select_car_layout.setOnClickListener {
            (context as OnSelectVehicleClickListener).onSelectVehicleClicked()
        }

        select_photo_layout.setOnClickListener {
            (context as OnSelectPhotoClickListener).onSelectPhotoClicked()
        }

        submit_announce_button.setOnClickListener {
            (context as OnAnnouncementSubmittedListener).onAnnouncementSubmitted()
        }


        mAnnouncementsViewModel.announcementStateLiveData.observe(this, Observer { status ->
            status.getContentIfNotHandled()?.let {
                when(status.peekContent()){
                        Status.RUNNING -> {

                        }
                        Status.SUCCESS -> {
                            val message =  R.string.announcement_success_message
                            Snackbar.make(view, message, Snackbar.LENGTH_LONG)
                                .show()

                            Handler().postDelayed({
                                activity?.supportFragmentManager?.popBackStack()
                            },1500)

                        }
                        Status.FAILED ->  {
                            val message =  R.string.announcement_failure_message
                            Snackbar.make(view, message, Snackbar.LENGTH_LONG)
                                .show()

                        }
            }
            }
        })
    }

    fun setVehicleProviderCredential(brand:Brand,version:Version){


        mAnnouncementsViewModel.newAnnouncementVersion.value = version
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

        mAnnouncementsViewModel.newAnnouncementPhotoUrl.value = url
        vehicle_image_view.visibility = View.VISIBLE
        Glide.with(context!!)
            .load(url)
            .into(vehicle_image_view)
    }


}
