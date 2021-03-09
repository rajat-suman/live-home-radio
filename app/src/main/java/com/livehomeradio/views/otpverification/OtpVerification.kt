package com.livehomeradio.views.otpverification

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.livehomeradio.R
import com.livehomeradio.databinding.OtpVerificationBinding
import com.livehomeradio.views.MainVM
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OtpVerification : Fragment(R.layout.otp_verification) {

    private var binding: OtpVerificationBinding? = null
    private val viewModel: OtpVerificationVM by viewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.bundle= arguments
        binding = OtpVerificationBinding.bind(view)
        binding?.viewModel = viewModel
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