package com.livehomeradio.networkcalls

import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface RetrofitApi {
    @POST(LOGIN_API)
    suspend fun login(
        @Body requestBody: RequestBody
    ): Response<BaseResponse<Any>>
}