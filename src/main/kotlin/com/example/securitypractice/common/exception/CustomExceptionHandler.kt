package com.example.securitypractice.common.exception

import com.example.securitypractice.common.status.BaseResponse
import com.example.securitypractice.common.status.ResultCode
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

// 전역 예외처리 핸들러 컨트롤러
@RestControllerAdvice
class CustomExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException::class)
    protected fun handleValidationException(ex: MethodArgumentNotValidException):
        ResponseEntity<BaseResponse<Map<String, String>>> {
        val errors = mutableMapOf<String, String>()
        ex.bindingResult.allErrors.forEach { error ->
            val fieldName = (error as FieldError).field
            val errorMessage = error.getDefaultMessage()
            errors[fieldName] = errorMessage ?: "Not Exception Message"
        }
        return ResponseEntity(
            BaseResponse(
                ResultCode.ERROR.name,
                errors,
                ResultCode.ERROR.message,
            ),
            HttpStatus.BAD_REQUEST,
        )
    }

    @ExceptionHandler(InvalidInputException::class)
    protected fun invalidInputException(ex: InvalidInputException):
        ResponseEntity<BaseResponse<Map<String, String>>> {
        val errors = mapOf(ex.fieldName to (ex.message ?: "Not Exception Message"))
        return ResponseEntity(
            BaseResponse(
                ResultCode.ERROR.name,
                errors,
                ResultCode.ERROR.message,
            ),
            HttpStatus.BAD_REQUEST,
        )
    }

    @ExceptionHandler(Exception::class)
    protected fun defaultException(ex: Exception):
        ResponseEntity<BaseResponse<Map<String, String>>> {
        val errors = mapOf("미처리 에러" to (ex.message ?: "Not Exception Message"))
        return ResponseEntity(
            BaseResponse(
                ResultCode.ERROR.name,
                errors,
                ResultCode.ERROR.message,
            ),
            HttpStatus.BAD_REQUEST,
        )
    }
}
