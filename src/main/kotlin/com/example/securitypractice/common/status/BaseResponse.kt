package com.example.securitypractice.common.status

data class BaseResponse<T> (
    val resultCode: String = ResultCode.SUCCESS.name,
    val data: T? = null,
    val message: String = ResultCode.SUCCESS.message
)