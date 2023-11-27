package com.example.securitypractice.member.repository

import com.example.securitypractice.member.entity.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<User, Long>{

    // email 중복 검사를 위해 필요
    fun findByEmail(email: String): User?
}