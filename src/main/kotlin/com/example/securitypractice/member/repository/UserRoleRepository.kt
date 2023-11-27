package com.example.securitypractice.member.repository

import com.example.securitypractice.member.entity.UserRole
import org.springframework.data.jpa.repository.JpaRepository

interface UserRoleRepository: JpaRepository<UserRole, Long>