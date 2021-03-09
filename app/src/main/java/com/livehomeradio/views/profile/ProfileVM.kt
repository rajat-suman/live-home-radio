package com.livehomeradio.views.profile

import androidx.lifecycle.ViewModel
import com.livehomeradio.datastore.DataStoreUtil
import com.livehomeradio.networkcalls.Repository
import com.livehomeradio.pref.PreferenceFile
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileVM @Inject constructor(
    private val repository: Repository,
    private val preferenceFile: PreferenceFile,
    private val dataStore: DataStoreUtil,
) : ViewModel() {}