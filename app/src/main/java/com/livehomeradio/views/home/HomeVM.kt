package com.livehomeradio.views.home

import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.livehomeradio.datastore.DataStoreUtil
import com.livehomeradio.datastore.JWT
import com.livehomeradio.models.DashBoardModel
import com.livehomeradio.networkcalls.ApiKeys
import com.livehomeradio.networkcalls.ApiProcessor
import com.livehomeradio.networkcalls.Repository
import com.livehomeradio.networkcalls.RetrofitApi
import com.livehomeradio.pref.PreferenceFile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class HomeVM @Inject constructor(
    private val repository: Repository,
    private val preferenceFile: PreferenceFile,
    private val dataStore: DataStoreUtil
) : ViewModel() {
    val model by lazy { ObservableField(DashBoardModel()) }
    fun hitDashBoard() = viewModelScope.launch {
        try {
            dataStore.readData(JWT) {
                repository.makeCall(
                    ApiKeys.DASHBOARD,
                    loader = false,
                    saveInCache = false,
                    requestProcessor = object : ApiProcessor<Response<DashBoardModel>> {
                        override suspend fun sendRequest(retrofitApi: RetrofitApi): Response<DashBoardModel> {
                            return retrofitApi.dashBoard("Bearer $it")
                        }

                        override fun onSuccess(res: Response<DashBoardModel>) {
                            model.set(res.body())
                        }
                    })

            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}