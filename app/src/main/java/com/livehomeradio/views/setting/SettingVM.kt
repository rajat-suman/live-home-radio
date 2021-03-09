package com.livehomeradio.views.setting

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
class SettingVM @Inject constructor(
    private val repository: Repository,
    private val preferenceFile: PreferenceFile,
    private val dataStore: DataStoreUtil,
) : ViewModel() {

    fun clickBack(view: View) {
        view.navigateBack()
    }


    fun onClick(view: View) {
        when (view.id) {

            R.id.tvChangePassword -> {
                view.navigateWithId(R.id.action_setting_to_changePassword, null)
            }

            R.id.tvPayment -> {
//                view.navigateWithId(R.id.action_setting_to_payments, null)
            }

            R.id.tvContactUs -> {
                view.navigateWithId(R.id.action_setting_to_contactUs, null)
            }

            R.id.tvAboutUs -> {

            }
        }
    }

}