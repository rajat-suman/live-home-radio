package com.livehomeradio.views.changepassword

import android.view.View
import androidx.lifecycle.ViewModel
import com.livehomeradio.datastore.DataStoreUtil
import com.livehomeradio.networkcalls.Repository
import com.livehomeradio.pref.PreferenceFile
import com.livehomeradio.utils.navigateBack
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ChangePasswordVM @Inject constructor(
    private val repository: Repository,
    private val preferenceFile: PreferenceFile,
    private val dataStore: DataStoreUtil,
) : ViewModel(){
    fun clickBack(view: View) {
        view.navigateBack()
    }

}