package com.example.securitypractice.member.controller

import com.example.securitypractice.common.authority.TokenInfo
import com.example.securitypractice.common.status.BaseResponse
import com.example.securitypractice.member.dto.LoginDto
import com.example.securitypractice.member.dto.UserDtoRequest
import com.example.securitypractice.member.service.UserService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/api/user")
@RestController
class UserController(
    private val userService: UserService,
) {

    @PostMapping("/signup")
    fun signUp(
        @RequestBody @Valid
        userDtoRequest: UserDtoRequest,
    ): BaseResponse<Unit> {
        val resultMessage: String = userService.signUp(userDtoRequest)
        return BaseResponse(message = resultMessage)
    }

    // 로그인
    @PostMapping("/login")
    fun login(
        @RequestBody @Valid
        loginDto: LoginDto,
    ): BaseResponse<TokenInfo> {
        val tokenInfo = userService.login(loginDto)
        return BaseResponse(data = tokenInfo)
    }
}
