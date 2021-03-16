package com.livehomeradio.networkcalls

data class BaseResponse<T>(
    var result: T? = null,
    var jwt: String? = null,
    var message: String? = null
)