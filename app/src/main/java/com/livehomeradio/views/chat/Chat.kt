package com.livehomeradio.views.chat

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.livehomeradio.R
import com.livehomeradio.databinding.ChatBinding
import com.livehomeradio.views.MainVM
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class Chat : Fragment(R.layout.chat) {

    private var binding: ChatBinding? = null
    private val viewModel: ChatVM by viewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = ChatBinding.bind(view)
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