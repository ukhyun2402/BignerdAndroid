package com.wookhyun.criminalintent.challenge

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.fragment.app.DialogFragment
import com.wookhyun.criminalintent.databinding.FragmentDialogZoomInBinding
import java.io.File

class PictureZoomInFragment(val imageFileName: String): DialogFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isCancelable = true
    }

    lateinit var binding: FragmentDialogZoomInBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDialogZoomInBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val photoFile = File(requireContext().applicationContext.filesDir, imageFileName)

        if(photoFile.exists()){
            binding.zoomInPicture.setImageURI(photoFile.toUri())
        }
    }
}