package com.livehomeradio.views.profile

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.livehomeradio.R
import com.livehomeradio.databinding.ProfileBinding
import com.livehomeradio.views.MainVM
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class Profile : Fragment(R.layout.profile) {

    var binding: ProfileBinding? = null
    val viewModel: ProfileVM by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = ProfileBinding.bind(view)
        binding?.vm = viewModel
    }

    override fun onResume() {
        super.onResume()
        setBottomSheet()
    }

    private fun setBottomSheet() {
        MainVM.homeListener?.isBottomSheet(false)
        MainVM.homeListener?.isHome(false)
        MainVM.homeListener?.isBookings(false)
        MainVM.homeListener?.isNotification(false)
        MainVM.homeListener?.isPayment(false)
    }
}