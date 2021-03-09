package com.livehomeradio.views.contactus

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.livehomeradio.R
import com.livehomeradio.databinding.ContactUsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ContactUs : Fragment(R.layout.contact_us) {

    private var binding: ContactUsBinding? = null
    private val viewModel: ContactUsVM by viewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = ContactUsBinding.bind(view)
        binding?.viewModel = viewModel
    }

}