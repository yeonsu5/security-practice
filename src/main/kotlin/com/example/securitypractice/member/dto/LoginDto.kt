package com.example.securitypractice.member.dto

import jakarta.validation.constraints.NotBlank

data class LoginDto(

    @field:NotBlank
    val email: String?,

    @field:NotBlank
    val password: String?,
)
