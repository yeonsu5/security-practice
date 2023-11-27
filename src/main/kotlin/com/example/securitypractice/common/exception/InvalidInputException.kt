package com.example.securitypractice.common.exception

import java.lang.RuntimeException

// @Valid 외에 필드값이 문제가 있어서 exception을 발생시킬 때 사용
class InvalidInputException(
    val fieldName: String = "",
    message: String = "Invalid Input"
) : RuntimeException(message)
