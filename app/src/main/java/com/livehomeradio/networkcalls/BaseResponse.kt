package com.livehomeradio.networkcalls

data class BaseResponse<T>(
    var result: T? = null,
    var message: String? = null,
)