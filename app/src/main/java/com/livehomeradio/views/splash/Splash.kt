package com.livehomeradio.views.splash

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.livehomeradio.R
import com.livehomeradio.databinding.SplashBinding
import com.livehomeradio.views.MainVM
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class Splash : Fragment(R.layout.splash) {
    private val viewModel: SplashVM by viewModels()
    private var binding: SplashBinding? = null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = SplashBinding.bind(view)
        binding?.viewModel = viewModel
        viewModel.setState(binding?.root!!)
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