package com.example.securitypractice.member.dto

import com.example.securitypractice.member.entity.User
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern

// 회원가입 시 입력받을 정보 : email, password, nickname
data class UserDtoRequest(

    @field:NotBlank
    @field:Email
    val email: String,

    @field:NotBlank
    @field:Pattern(
        regexp = "^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[!@#\$%^&*])[a-zA-Z0-9!@#\$%^&*]{8,20}\$",
        message = "영문, 숫자, 특수문자(!,@,#,$,%,^,&,*)를 포함한 8~20자리로 입력해주세요",
    )
    val password: String,

    @field:NotBlank
    val nickname: String,
) {
    fun toEntity(): User =
        User(email, password, nickname)
}
