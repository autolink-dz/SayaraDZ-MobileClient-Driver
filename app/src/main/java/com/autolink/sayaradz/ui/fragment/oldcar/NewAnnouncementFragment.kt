package com.autolink.sayaradz.ui.fragment.oldcar

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation


import com.autolink.sayaradz.R
import com.autolink.sayaradz.databinding.FragmentNewAnnouncementBinding
import android.content.DialogInterface
import android.app.AlertDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder


class NewAnnouncementFragment : Fragment() {

    lateinit var binding: FragmentNewAnnouncementBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(
            inflater,R.layout.fragment_new_announcement,container,false
        )

        binding.carImage.setOnClickListener{
            uploadPhoto()
        }

        binding.confirmBtn.setOnClickListener{
            confirmDialog()
        }


        return binding.root
    }

    private fun uploadPhoto() {

        startActivityForResult(getUploadIntent(),1000)
    }

    private fun getUploadIntent(): Intent? {
        val shareIntent=Intent(Intent.ACTION_GET_CONTENT)
        shareIntent.setType("image/*")
        return shareIntent

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            binding.carImage.setImageURI(data?.data)
    }

    private fun confirmDialog() {
        val builder = MaterialAlertDialogBuilder(context)
        builder
            .setTitle("Confirmation")
            .setMessage("Voulez vous confirmer l'annonce que vous venez de creéer ? ")
            .setPositiveButton("Confirmer", DialogInterface.OnClickListener { dialog, id ->
                // Yes-code
            })
            .setNegativeButton("Annuler", DialogInterface.OnClickListener { dialog, id -> dialog.cancel() })
            .show()
    }

}
