package com.livehomeradio.views.chat

import android.view.View
import androidx.lifecycle.ViewModel
import com.livehomeradio.datastore.DataStoreUtil
import com.livehomeradio.networkcalls.Repository
import com.livehomeradio.pref.PreferenceFile
import com.livehomeradio.utils.navigateBack
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ChatVM @Inject constructor(
    private val repository: Repository,
    private val preferenceFile: PreferenceFile,
    private val dataStore: DataStoreUtil,
) : ViewModel() {
    val adapter by lazy { ChatAdapter() }

    fun clickBack(view: View) {
        view.navigateBack()
    }

}