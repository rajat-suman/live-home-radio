package com.livehomeradio.views

import android.util.Log
import android.view.View
import androidx.databinding.ObservableBoolean
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.livehomeradio.R
import com.livehomeradio.datastore.DataStoreUtil
import com.livehomeradio.datastore.LOGIN_DATA
import com.livehomeradio.networkcalls.*
import com.livehomeradio.pref.PreferenceFile
import com.livehomeradio.utils.HomeListener
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class MainVM @Inject constructor(
    private val repository: Repository,
    private val preferenceFile: PreferenceFile,
    private val dataStore: DataStoreUtil,
) : ViewModel(), HomeListener {

    lateinit var navController: NavController

    val isBottom by lazy { ObservableBoolean(false) }
    val isHome by lazy { ObservableBoolean(false) }
    val isBookings by lazy { ObservableBoolean(false) }
    val isNotifications by lazy { ObservableBoolean(false) }
    val isPayments by lazy { ObservableBoolean(false) }

    companion object {
        var homeListener: HomeListener? = null
    }

    init {
        homeListener = this
    }

    //Clicks
    fun clickBooking(view: View) {
       /* if (!isBookings.get())
            navController.navigate(R.id.myBookings)*/
    }

    fun clickHome(view: View) {
     /*   if (!isHome.get())
            navController.navigate(R.id.home2)*/
    }

    fun clickNotifications(view: View) {
        if (!isNotifications.get())
            navController.navigate(R.id.notifications)
    }

    fun clickPayments(view: View) {
        if (!isPayments.get())
            navController.navigate(R.id.setting)
    }

    fun observerData() = viewModelScope.launch {

        dataStore.saveData(LOGIN_DATA, "login${Math.random()}")

        val json = JSONObject()
        json.put("email", "rajatsuman@yopmail.com")
        json.put("password", "Rajat@123")
        json.put("deviceType", "ANDROID")
        json.put("deviceToken", "qwertyuiop")

        val requestBody =
            json.toString().toRequestBody("application/json".toMediaTypeOrNull())

        repository.makeCall(
            ApiKeys.LOGIN,
            loader = true,
            saveInCache = true,
            requestProcessor = object :
                ApiProcessor<Response<BaseResponse<Any>>> {
                override suspend fun sendRequest(retrofitApi: RetrofitApi): Response<BaseResponse<Any>> {
                    return retrofitApi.login(requestBody)
                }

                override fun onResponse(res: Response<BaseResponse<Any>>) {
                    /*  preferenceFile.storeKey("login", "yes")
                              dataStore.saveData(THEME_KEY, "YESS")*/
                    //                Toast.makeText(context, "Login Success", Toast.LENGTH_LONG).show()
                    Log.d("successBodyOnResponse", "====${res.body()}")

                    GlobalScope.launch {
                        dataStore.readData(LOGIN_DATA) {
                            Log.d("readData", "====${it}")

                        }

                    }

                }
            })
    }

    private fun startLongRunningTask(): Int {
        var lastVal = 0
        var lastVal2 = 0
        viewModelScope.launch {
            try {
                val resultOneDeferred = listOf(
                    async {
                        for (i in 0..1000000)
                            lastVal += i
                    },
                    async { lastVal2 = 3000 }
                )
                resultOneDeferred.awaitAll()

            } catch (e: Exception) {
            }
        }

        return lastVal + lastVal2
    }

    fun getInt(value2: (Int) -> Unit) {
        value2(0)
    }

    override fun isBottomSheet(boolean: Boolean) {
        isBottom.set(boolean)
    }

    override fun isHome(boolean: Boolean) {
        isHome.set(boolean)
    }

    override fun isBookings(boolean: Boolean) {
        isBookings.set(boolean)
    }

    override fun isNotification(boolean: Boolean) {
        isNotifications.set(boolean)
    }

    override fun isPayment(boolean: Boolean) {
        isPayments.set(boolean)
    }

}