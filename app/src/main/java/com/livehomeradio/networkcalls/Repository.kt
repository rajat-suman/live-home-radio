package com.livehomeradio.networkcalls

import android.util.Log
import com.livehomeradio.R
import com.livehomeradio.utils.hideProgress
import com.livehomeradio.utils.sessionExpired
import com.livehomeradio.utils.showNegativeAlerter
import com.livehomeradio.utils.showProgress
import com.livehomeradio.views.BaseActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Response
import javax.inject.Inject

class Repository @Inject constructor(
    private val retrofitApi: RetrofitApi,
    private val cacheUtil: CacheUtil
) {
    fun <T> makeCall(
        apiKey: ApiKeys,
        loader: Boolean,
        saveInCache: Boolean,
        requestProcessor: ApiProcessor<Response<T>>
    ) {
        val activity = BaseActivity.contextIs.get()!!
        if (cacheUtil.snapshot().containsKey(apiKey)) {
            Log.d("cacheUtil", "========${cacheUtil[apiKey]}")
            requestProcessor.onSuccess(cacheUtil[apiKey] as Response<T>)
            return
        }
        if (loader) {
            activity.showProgress()
        }

        val dataResponse: Flow<Response<Any>> = flow {
            val response =
                requestProcessor.sendRequest(retrofitApi) as Response<Any>
            emit(response)
        }.flowOn(Dispatchers.IO)

        CoroutineScope(Dispatchers.Main).launch {
            dataResponse.catch { exception ->
                exception.printStackTrace()
                hideProgress()
                activity.showNegativeAlerter(exception.message?:"")
            }.collect { response ->
                Log.d("resCodeIs", "====${response.code()}")
                hideProgress()
                when {
                    response.code() in 100..199 -> {
                        /**Informational*/
                        requestProcessor.onInfo(
                            activity.resources?.getString(R.string.some_error_occured) ?: ""
                        )
                    }
                    response.isSuccessful -> {
                        /**Success*/
                        Log.d("successBody", "====${response.body()}")
                        if (saveInCache)
                            cacheUtil.put(apiKey, response)
                        requestProcessor.onSuccess(response as Response<T>)
                    }
                    response.code() in 300..399 -> {
                        /**Redirection*/
                        requestProcessor.onRedirect(
                            activity.resources?.getString(R.string.some_error_occured) ?: ""
                        )
                    }
                  /*  response.code() == 401 -> {
                        *//**ClientErrors*//*
                        Log.d("errorBody", "====${response.errorBody()?.string()}")
                        activity.sessionExpired()
                        requestProcessor.onError("unAuthorized")
                    }*/
                    response.code() == 404 -> {
                        /**ClientErrors*/
                        requestProcessor.onError(
                            activity.resources?.getString(R.string.some_error_occured) ?: ""
                        )
                        activity.showNegativeAlerter(
                            activity.resources?.getString(R.string.some_error_occured) ?: ""
                        )
                    }
                    response.code() in 500..599 -> {
                        /**ServerErrors*/
                        requestProcessor.onError(
                            activity.resources?.getString(R.string.some_error_occured) ?: ""
                        )
                        activity.showNegativeAlerter(
                            activity.resources?.getString(R.string.some_error_occured) ?: ""
                        )
                    }
                    else -> {
                        /**ClientErrors*/
                        val res = response.errorBody()!!.string()
                        val jsonObject = JSONObject(res)
                        when {
                            jsonObject.has("title") -> {
                                requestProcessor.onError(jsonObject.getString("title"))
                                activity
                                    .showNegativeAlerter(jsonObject.getString("title"))
                            }
                            else -> {
                                requestProcessor.onError(
                                    activity.resources?.getString(R.string.some_error_occured)
                                        ?: ""
                                )
                                activity.showNegativeAlerter(
                                    activity.resources?.getString(R.string.some_error_occured)
                                        ?: ""
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
