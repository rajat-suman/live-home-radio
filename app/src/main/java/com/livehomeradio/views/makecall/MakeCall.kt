package com.livehomeradio.views.makecall

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.livehomeradio.R
import com.livehomeradio.databinding.MakeCallBinding
import com.livehomeradio.recycleradapter.RecyclerAdapter
import com.livehomeradio.utils.ContactsUtil
import com.livehomeradio.utils.navigateBack
import com.livehomeradio.views.BaseActivity
import com.livehomeradio.views.MainVM
import com.livehomeradio.views.home.HomeVM
import com.livehomeradio.views.login.LoginVM
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MakeCall : Fragment() {
    private var binding: MakeCallBinding? = null
    private val viewModel: LoginVM by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = MakeCallBinding.inflate(inflater)
        setUpData()
        return binding?.root
    }

    private fun setUpData() {
        try {
            val adapter = RecyclerAdapter<ContactsUtil.ContactsModel>(R.layout.call_item)
            binding?.ivCross?.setOnClickListener { it.navigateBack() }
            binding?.rvContacts?.adapter = adapter
            binding?.etSearch?.doOnTextChanged { text, _, _, _ ->
                if (text.toString().trim().isNotEmpty()) {
                    val filteredList = BaseActivity.contactsList.filter {
                        it.name.startsWith(
                            text.toString().trim(),
                            ignoreCase = true
                        )
                    }
                    adapter.addItems(filteredList)
                } else {
                    adapter.addItems(BaseActivity.contactsList)
                }
            }

            val etPhoneNumber = binding?.etPhoneNumber!!
            binding?.btCallDial?.setOnClickListener {
                if (etPhoneNumber.text.toString().trim().isNotEmpty()) {
                    HomeVM.nexmoCallListener?.makeCall(etPhoneNumber.text.toString().trim())
                    it.navigateBack()
                }
            }

            adapter.setOnItemClick(object : RecyclerAdapter.OnItemClick {
                override fun onClick(view: View, position: Int, type: String) {
                    HomeVM.nexmoCallListener?.makeCall(adapter.items[position].number)
                    view.navigateBack()
                }
            })

            adapter.addItems(BaseActivity.contactsList)

        } catch (e: Exception) {
            e.printStackTrace()
        }
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