package com.example.securitypractice.common.service

import com.example.securitypractice.member.entity.User
import com.example.securitypractice.member.repository.UserRepository
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class CustomUserDetailsService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
) : UserDetailsService {
    override fun loadUserByUsername(email: String): UserDetails =
        userRepository.findByEmail(email)
            ?.let { createUserDetails(it) }
            ?: throw UsernameNotFoundException("해당하는 유저를 찾을 수 없습니다.")

    private fun createUserDetails(user: User): UserDetails =
        org.springframework.security.core.userdetails.User(
            user.email,
            passwordEncoder.encode(user.password),
            user.UserRole!!.map { SimpleGrantedAuthority("ROLE_${it.role}") },
        )
}

// password가 암호화되어 저장이 안됨
