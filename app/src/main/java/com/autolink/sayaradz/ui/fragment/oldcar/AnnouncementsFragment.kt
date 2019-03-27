package com.autolink.sayaradz.ui.fragment.oldcar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.autolink.sayaradz.R
import com.autolink.sayaradz.databinding.FragmentAnnouncementsBinding


class AnnouncementsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding: FragmentAnnouncementsBinding = DataBindingUtil.inflate(
            inflater,R.layout.fragment_announcements,container,false
        )

        binding.announcementFab.setOnClickListener(

            Navigation.createNavigateOnClickListener(
                R.id.action_announcementsFragment_to_newAnnouncementFragment
            )

        )

        return binding.root
    }

}
