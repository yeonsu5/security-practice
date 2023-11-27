package com.example.securitypractice.common.authority

data class TokenInfo(
    val grantType: String,
    val accessToken: String,
)
