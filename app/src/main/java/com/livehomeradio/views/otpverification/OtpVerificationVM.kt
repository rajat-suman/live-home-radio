package com.livehomeradio.views.otpverification

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModel
import com.livehomeradio.R
import com.livehomeradio.datastore.DataStoreUtil
import com.livehomeradio.networkcalls.Repository
import com.livehomeradio.pref.PreferenceFile
import com.livehomeradio.utils.navigateBack
import com.livehomeradio.utils.navigateWithId
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class OtpVerificationVM @Inject constructor(
    private val repository: Repository,
    private val preferenceFile: PreferenceFile,
    private val dataStore: DataStoreUtil,
) : ViewModel() {
    var bundle: Bundle? = null
    fun clickBack(view: View) {
        view.navigateBack()
    }

    fun resend(view: View) {
        view.navigateBack()
    }
}