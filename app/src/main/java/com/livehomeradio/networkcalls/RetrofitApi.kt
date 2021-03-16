package com.livehomeradio.networkcalls

import com.livehomeradio.models.DashBoardModel
import com.livehomeradio.models.LoginModel
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface RetrofitApi {
    @POST(LOGIN_API)
    suspend fun login(
        @Body requestBody: RequestBody
    ): Response<LoginModel>


    @PUT(CHANGE_PASSWORD)
    suspend fun changePassword(
        @Header("Authorization") header:String,
       @Query("oldPassword") oldPassword:String?,
       @Query("password") password:String?
    ): Response<Any>


    @GET(DASHBOARD)
    suspend fun dashBoard(
        @Header("Authorization") header:String,
    ): Response<DashBoardModel>
}