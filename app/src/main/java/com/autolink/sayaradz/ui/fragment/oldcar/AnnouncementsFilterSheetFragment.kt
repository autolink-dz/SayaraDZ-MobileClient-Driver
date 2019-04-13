package com.autolink.sayaradz.ui.fragment.oldcar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.autolink.sayaradz.R
import com.autolink.sayaradz.ui.fragment.RoundedBottomSheetDialogFragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class AnnouncementsFilterSheetFragment:RoundedBottomSheetDialogFragment(){

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_filter_announcements,container,false)
    }

}
