package com.livehomeradio.views.setting

import android.app.AlertDialog
import android.view.View
import androidx.lifecycle.ViewModel
import com.livehomeradio.R
import com.livehomeradio.datastore.DataStoreUtil
import com.livehomeradio.datastore.LOGIN_DATA
import com.livehomeradio.datastore.REMEMBER
import com.livehomeradio.networkcalls.Repository
import com.livehomeradio.pref.PreferenceFile
import com.livehomeradio.utils.navigateBack
import com.livehomeradio.utils.navigateWithId
import com.livehomeradio.views.BaseActivity
import com.livehomeradio.views.home.HomeVM
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

     fun showExit(view: View) {
        val context = BaseActivity.contextIs.get()!!
        val aD = AlertDialog.Builder(context)
        aD.setTitle(context.getString(R.string.logout_message))
        aD.setCancelable(false)
        aD.setPositiveButton(context.getString(R.string.ok)) { dialogInterface, i ->
            dialogInterface.cancel()
            dialogInterface.dismiss()
            dataStore.removeKey(LOGIN_DATA) {

            }
            dataStore.removeKey(REMEMBER) {
                HomeVM.nexmoCallListener?.onLogout()
                view.navigateWithId(R.id.action_setting_to_login, null)
            }

        }
        aD.setNegativeButton(context.getString(R.string.cancel)) { dialogInterface, i ->
            dialogInterface.cancel()
            dialogInterface.dismiss()
        }
        aD.create()
        aD.show()
    }


}