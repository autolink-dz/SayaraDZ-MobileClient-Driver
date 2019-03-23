package com.autolink.sayaradz.ui.fragment.newcar

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.autolink.sayaradz.R
import com.autolink.sayaradz.vo.Version
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.fragment_version_profile.*
import kotlinx.android.synthetic.main.item_list_default_option.view.*
import kotlinx.android.synthetic.main.item_list_spec.view.*
import android.content.res.ColorStateList
import android.util.Log
import android.widget.CheckBox
import androidx.transition.TransitionInflater
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.autolink.sayaradz.databinding.FragmentVersionProfileBinding
import com.autolink.sayaradz.repository.utils.Status
import com.autolink.sayaradz.util.RepositoryKey
import com.autolink.sayaradz.util.getViewModel
import com.autolink.sayaradz.viewmodel.TariffViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.item_list_non_default_option.view.*


//TODO: follow a version
class VersionProfileFragment:Fragment(){


    companion object {
        private const val TAG  = "VersionProfileFragment"
        const val VERSION_OBJECT_ARG_KEY = "version"
    }

    private val mVersion by lazy {
        arguments!![VERSION_OBJECT_ARG_KEY] as Version
    }
    private val mTariffViewModel by lazy {
        getViewModel(RepositoryKey.TARIFF_REPOSITORY) as TariffViewModel
    }

    private lateinit var mBinding: FragmentVersionProfileBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedElementEnterTransition = TransitionInflater.from(context).inflateTransition(R.transition.move)



    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?):View {
        mBinding  = DataBindingUtil.inflate(inflater,R.layout.fragment_version_profile,container,false)
        return mBinding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mBinding.setLifecycleOwner(this)

        mTariffViewModel.setVersion(mVersion)


        mBinding.tariffviewmodel = mTariffViewModel

        setUpProfile()

        mTariffViewModel.orderStateLiveData.observe(this, Observer { status ->
            status.getContentIfNotHandled()?.let { // Only proceed if the event has never been handled //

                val message = if(status.peekContent() == Status.SUCCESS)
                    R.string.order_success_message
                else
                    R.string.order_failure_message

                Snackbar.make(view, message, Snackbar.LENGTH_LONG)
                    .show()
            }
        })



        mTariffViewModel.suggestedOptionsPriceLiveData.observe(this, Observer {

            it.forEach{
                suggested_options_list_container.findViewWithTag<View>(it.key).apply {
                    this.suggested_option_state.isEnabled = true
                    this.suggested_option_price.text  = it.value.toString()


                }

            }

        })
    }


    @SuppressLint("ResourceType")
    fun setUpProfile(){

        activity?.findViewById<TextView>(R.id.toolbar_title)?.text = mVersion.name

        Glide.with(context!!)
            .load(mVersion.photoURL)
            .into(version_image)

        mVersion.specs.map {
            val itemView  = layoutInflater.inflate(R.layout.item_list_spec,null)
            itemView.title.text = it.key
            itemView.content.content.text = it.value
            specs_list_container.addView(itemView)
        }

        mVersion.options.forEach {
            val itemView  = layoutInflater.inflate(R.layout.item_list_default_option,null)
            itemView.item_title.text = it.name
            options_list_container.addView(itemView)
        }

        mVersion.nonSupportedOptions.forEach {

            val itemView  = layoutInflater.inflate(R.layout.item_list_non_default_option,null).apply {
                tag  = it.code
                suggested_option_title.text = it.name

                suggested_option_state.setOnClickListener {
                    val state  =  (it as CheckBox).isChecked
                    val optionCode      = (it.parent as ViewGroup).tag as String
                    mTariffViewModel.setSuggestedOption(optionCode,state)
                }
            }

            suggested_options_list_container.addView(itemView)
        }

        mVersion.colors.forEachIndexed {index,color ->

            val radioButton =  layoutInflater.inflate(R.layout.item_colors_list,null) as RadioButton
            val colorHex = Color.parseColor(color.hex)

            radioButton.id = colorHex
            radioButton.buttonTintList = ColorStateList(
                arrayOf(

                    intArrayOf(-android.R.attr.state_enabled), //disabled
                    intArrayOf(android.R.attr.state_enabled) //enabled
                ),
                intArrayOf(

                    colorHex ,//disabled
                    colorHex
                )
            )


            if(index == 0) {
                radioButton.isChecked =  true
                mTariffViewModel.setColorCode(color.code)
            }

            colors_list_container.addView(radioButton)

        }

        colors_list_container.setOnCheckedChangeListener { radioGroup, i ->
            val hexColor = String.format("#%06X", 0xFFFFFF and i)

            val color  = mVersion.colors.findLast{
                it.hex == hexColor
            }!!

            mTariffViewModel.setColorCode(color.code)
        }

        order_button.setOnClickListener {
            mTariffViewModel.setOrder()
        }

    }



}