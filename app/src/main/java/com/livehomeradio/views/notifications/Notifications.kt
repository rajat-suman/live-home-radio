package com.livehomeradio.views.notifications

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.livehomeradio.R
import com.livehomeradio.databinding.NotificationsBinding
import com.livehomeradio.views.MainVM
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class Notifications : Fragment(R.layout.notifications) {
    private var binding: NotificationsBinding? = null
    private val viewModel: NotificationsVM by viewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = NotificationsBinding.bind(view)
        binding?.viewModel = viewModel
    }

    override fun onResume() {
        super.onResume()
        setBottomSheet()
    }

    private fun setBottomSheet() {
        MainVM.homeListener?.isBottomSheet(true)
        MainVM.homeListener?.isHome(false)
        MainVM.homeListener?.isBookings(false)
        MainVM.homeListener?.isNotification(true)
        MainVM.homeListener?.isPayment(false)
    }

}