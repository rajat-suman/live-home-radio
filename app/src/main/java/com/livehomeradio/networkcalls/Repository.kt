package com.livehomeradio.networkcalls

import android.content.Context
import android.util.Log
import com.livehomeradio.R
import com.livehomeradio.utils.hideProgress
import com.livehomeradio.utils.sessionExpired
import com.livehomeradio.utils.showNegativeAlerter
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Response
import javax.inject.Inject


class Repository @Inject constructor(
    @ApplicationContext private val activity: Context,
    private val retrofitApi: RetrofitApi,
    private val cacheUtil: CacheUtil
) {
    fun <T> makeCall(
        apiKey: ApiKeys,
        loader: Boolean,
        saveInCache: Boolean,
        requestProcessor: ApiProcessor<Response<BaseResponse<T>>>
    ) {

        if (cacheUtil.snapshot().containsKey(apiKey)) {
            Log.e("cacheUtil", "========${cacheUtil[apiKey]}")
            requestProcessor.onResponse(cacheUtil[apiKey] as Response<BaseResponse<T>>)
            return
        }
        /*  if (loader)
                     activity.showProgress()*/
        val dataResponse: Flow<Response<BaseResponse<Any>>> = flow {
            val response =
                requestProcessor.sendRequest(retrofitApi) as Response<BaseResponse<Any>>
            emit(response)
        }.flowOn(Dispatchers.IO)

        CoroutineScope(Dispatchers.Main).launch {
            hideProgress()
            dataResponse.catch { exception -> Log.e("exception", "===$exception") }
                .collect { response ->
                    Log.d("resCodeIs", "====${response.code()}")
                    when {
                        response.code() == 401 -> {
                            Log.d("errorBody", "====${response.errorBody()?.string()}")
                            activity.sessionExpired()
                            requestProcessor.onError("unAuthorized")
                        }
                        response.isSuccessful -> {
                            Log.d("successBody", "====${response.body()}")
                            if (saveInCache)
                                cacheUtil.put(apiKey, response)
                            requestProcessor.onResponse(response as Response<BaseResponse<T>>)
                        }
                        else -> {
                            val res = response.errorBody()!!.string()
                            Log.d("errorBody", "====${response.errorBody()?.string()}")
                            val jsonObject = JSONObject(res)
                            when {
                                jsonObject.has("message") -> {
                                    requestProcessor.onError(jsonObject.getString("message"))
                                    activity
                                        .showNegativeAlerter(jsonObject.getString("message"))
                                }
                                else -> {
                                    requestProcessor.onError(
                                        activity.resources?.getString(R.string.server_error) ?: ""
                                    )
                                    activity.showNegativeAlerter(
                                        activity.resources?.getString(R.string.server_error) ?: ""
                                    )
                                }
                            }
                        }
                    }
                }
        }
    }
}