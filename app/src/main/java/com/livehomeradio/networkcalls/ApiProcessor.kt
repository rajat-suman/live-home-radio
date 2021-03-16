package com.livehomeradio.networkcalls

interface ApiProcessor<T> {

    suspend fun sendRequest(retrofitApi: RetrofitApi): T
    fun onSuccess(res: T)
    fun onError(message: String) {}
    fun onInfo(message: String) {}
    fun onRedirect(message: String) {}

}