package com.livehomeradio.views.login

import android.view.View
import androidx.lifecycle.ViewModel
import com.livehomeradio.R
import com.livehomeradio.datastore.DataStoreUtil
import com.livehomeradio.networkcalls.Repository
import com.livehomeradio.pref.PreferenceFile
import com.livehomeradio.utils.navigateWithId
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginVM @Inject constructor(
    private val repository: Repository,
    private val preferenceFile: PreferenceFile,
    private val dataStore: DataStoreUtil,
) : ViewModel(){


    fun clickLogin(view:View){
        view.navigateWithId(R.id.action_login_to_home2, null)
    }
    fun clickForgot(view:View){
        view.navigateWithId(R.id.action_login_to_forgotPassword, null)
    }

}