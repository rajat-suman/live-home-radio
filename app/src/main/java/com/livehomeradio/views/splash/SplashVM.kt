package com.livehomeradio.views.splash

import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.lifecycle.ViewModel
import com.livehomeradio.R
import com.livehomeradio.datastore.DataStoreUtil
import com.livehomeradio.datastore.JWT
import com.livehomeradio.datastore.REMEMBER
import com.livehomeradio.networkcalls.Repository
import com.livehomeradio.pref.PreferenceFile
import com.livehomeradio.utils.navigateWithId
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SplashVM @Inject constructor(
    private val repository: Repository,
    private val preferenceFile: PreferenceFile,
    private val dataStore: DataStoreUtil
) : ViewModel() {
    fun setState(view: View) {
        Handler(Looper.myLooper()!!).postDelayed({
            dataStore.readData(REMEMBER) {
                if (it == true) {
                    view.navigateWithId(R.id.action_splash_to_home2, null)
                } else {
                    view.navigateWithId(R.id.action_splash_to_login, null)
                }
            }
        }, 2000)
    }
}