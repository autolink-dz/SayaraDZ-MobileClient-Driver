package com.autolink.sayaradz.ui.fragment.oldcar

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.WhichButton
import com.afollestad.materialdialogs.actions.setActionButtonEnabled
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.afollestad.materialdialogs.input.getInputField
import com.afollestad.materialdialogs.input.input
import com.autolink.sayaradz.R
import com.autolink.sayaradz.repository.utils.Status
import com.autolink.sayaradz.util.GlideApp
import com.autolink.sayaradz.util.dp2px
import com.autolink.sayaradz.viewmodel.AnnouncementsViewModel
import com.autolink.sayaradz.viewmodel.UserViewModel
import com.autolink.sayaradz.vo.Announcement
import com.autolink.sayaradz.vo.Version
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.fragment_announcement_profile.*
import java.text.SimpleDateFormat

class AnnouncementProfileFragment:Fragment(){



    companion object {
        private const val TAG  = "AnnouncementProfile"
        const val ANNOUNCEMENT_OBJECT_ARG_KEY = "announcement"
    }


    private val mAnnouncementsViewModel by lazy {
        activity?.run {
            ViewModelProviders.of(this).get(AnnouncementsViewModel::class.java)
        } ?: throw Exception("Invalid Activity")
    }

    private val mDialogView by lazy {
        layoutInflater.inflate(R.layout.dialog_view_offer_price,null,false)
    }

    private val mAnnouncement by lazy {
        arguments!![ANNOUNCEMENT_OBJECT_ARG_KEY] as Announcement
    }

    private val mUserViewModel by lazy {
        activity?.run {
            ViewModelProviders.of(this).get(UserViewModel::class.java)
        } ?: throw Exception("Invalid Activity")
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = null
    }



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?)
        = inflater.inflate(R.layout.fragment_announcement_profile,container,false)

    @SuppressLint("SimpleDateFormat")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity?.findViewById<TextView>(R.id.toolbar_title)?.text = ""
        setUpEnterTransition(mAnnouncement.id)
        setUpLayoutInput(mAnnouncement)
        mAnnouncementsViewModel.offerStateLiveData.observe(this, Observer {status ->

            status.getContentIfNotHandled()?.let { // Only proceed if the event has never been handled //

                val message = if(status.peekContent() == Status.SUCCESS)
                    R.string.offer_success_message
                else
                    R.string.offer_failure_message

                Snackbar.make(view, message, Snackbar.LENGTH_LONG)
                    .show()
            }
        })

        }



    private fun setUpLayoutInput(announcement: Announcement){


        with(announcement){
            GlideApp.with(this@AnnouncementProfileFragment)
                .load(photoURL)
                .into(profile_announce_image_view)

            GlideApp.with(this@AnnouncementProfileFragment)
                .load(brand.photoURL)
                .into(profile_brand_image_view)
            profile_vehicle_name_text_view.text = version.name

            profile_distance_text_view.text = context!!.getString(R.string.distance_placeholder,distance.toInt().toString())

            profile_year_text_view.text  = year

            profile_price_text_view.text = context!!.getString(R.string.currency_placeholder,price.toInt().toString())

            GlideApp.with(this@AnnouncementProfileFragment)
                .load(owner.photoURL)
                .apply(RequestOptions().centerCrop().transform(RoundedCorners(dp2px(view!!.context,35F))))
                .into(profile_owner_image_view)


            profile_owner_text_view.text = owner.name

            profile_time_text_view.text = context!!.resources.getString(R.string.published_title,SimpleDateFormat("dd-MM-yyyy").format(date).toString())

            announce_desc_text_view.text  = description


            mDialogView.findViewById<TextView>(R.id.dialog_body_text_view).text = context!!.resources.getString(R.string.offer_dialog_message,mAnnouncement.price.toInt().toString())
            offer_button.setOnClickListener {
                Log.d("TAG","I am here")
                MaterialDialog(context!!).show {
                    customView(view = mDialogView)
                    noAutoDismiss()
                    negativeButton(R.string.cancel_title){
                        dismiss()
                    }
                    positiveButton(R.string.send_title){
                        val view = it.getCustomView()
                        val textInputLayout = view.findViewById<TextInputLayout>(R.id.price_text_input_layout)
                        val price  = textInputLayout.editText!!.text.toString().toFloatOrNull() ?: 0F
                        val error   = if (price >= mAnnouncement.price) {
                                    dismiss()
                                    mAnnouncementsViewModel.setOffer(mAnnouncement,mUserViewModel.getCarDriverLiveData().value!!,price)
                                    null
                             }else{
                                    context.resources.getString(R.string.non_valid_offer_error_message)
                             }
                        textInputLayout.error = error

                    }

                }
            }

        }
    }

    private fun setUpEnterTransition(id: String){

        ViewCompat.setTransitionName(profile_announce_image_view,"announce_image_view_$id")
        ViewCompat.setTransitionName(profile_brand_image_view,"brand_image_view_$id")
        ViewCompat.setTransitionName(profile_vehicle_name_text_view,"vehicle_name_text_view_$id")

    }

}