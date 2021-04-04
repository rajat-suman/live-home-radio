package com.livehomeradio.views.makecall

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import com.livehomeradio.databinding.MakeCallBinding
import com.livehomeradio.utils.navigateBack
import com.livehomeradio.views.BaseActivity
import com.livehomeradio.views.MainVM
import com.livehomeradio.views.home.HomeVM
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


@AndroidEntryPoint
class MakeCall : Fragment() {
    private var binding: MakeCallBinding? = null
    private val adapter = ContactsAdapter()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = MakeCallBinding.inflate(inflater)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpData()
        Handler(Looper.getMainLooper()).postDelayed({
            getContacts()
        },500)
    }

    private fun getContacts() {
        CoroutineScope(Dispatchers.IO).launch {
            BaseActivity.db.contactsDao().getContacts().catch {
            }.collect {
                CoroutineScope(Dispatchers.Main).launch {
                    adapter.addItems(it)
                }
            }
        }

    }

    private fun setUpData() {
        try {
            binding?.ivCross?.setOnClickListener { it.navigateBack() }
            binding?.rvContacts?.adapter = adapter
            binding?.etSearch?.doOnTextChanged { text, _, _, _ ->
                if (text.toString().trim().isNotEmpty()) {
                    CoroutineScope(Dispatchers.IO).launch {
                        BaseActivity.db.contactsDao().searchContacts("%${text.toString().trim()}%")
                            .catch { }.collect {
                                CoroutineScope(Dispatchers.Main).launch {
                                    adapter.addItems(it)
                                }
                            }
                    }
                } else {
                    getContacts()
                }
            }
            val etPhoneNumber = binding?.etPhoneNumber!!
            binding?.btCallDial?.setOnClickListener {
                if (etPhoneNumber.text.toString().trim().isNotEmpty()) {
                    HomeVM.nexmoCallListener?.makeCall(etPhoneNumber.text.toString().trim())
                    it.navigateBack()
                }
            }
            adapter.initClick { position, view ->
                val number = adapter.getAllItems()[position].number
                HomeVM.nexmoCallListener?.makeCall(number.trim())
                view.navigateBack()
            }

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