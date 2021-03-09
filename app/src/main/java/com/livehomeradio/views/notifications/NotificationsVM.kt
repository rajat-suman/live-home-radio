package com.livehomeradio.views.notifications

import androidx.lifecycle.ViewModel
import com.livehomeradio.R
import com.livehomeradio.datastore.DataStoreUtil
import com.livehomeradio.networkcalls.Repository
import com.livehomeradio.pref.PreferenceFile
import com.livehomeradio.recycleradapter.DummyModel
import com.livehomeradio.recycleradapter.RecyclerAdapter
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class NotificationsVM @Inject constructor(
    private val repository: Repository,
    private val preferenceFile: PreferenceFile,
    private val dataStore: DataStoreUtil,
) : ViewModel() {
    // TODO: Implement the ViewModel
    val recentAdapter by lazy { RecyclerAdapter<DummyModel>(R.layout.notification_adapter) }
    val olderAdapter by lazy { RecyclerAdapter<DummyModel>(R.layout.notification_adapter) }

    init {
        recentAdapter.addItems(
            listOf(
                DummyModel(true),
                DummyModel(true)
            )
        )
        olderAdapter.addItems(
            listOf(
                DummyModel(false),
                DummyModel(false),
                DummyModel(false),
                DummyModel(false),
                DummyModel(false),
                DummyModel(false),
            )
        )
    }
}