package com.example.securitypractice.member.service

import com.example.securitypractice.common.authority.JwtTokenProvider
import com.example.securitypractice.common.authority.TokenInfo
import com.example.securitypractice.common.exception.InvalidInputException
import com.example.securitypractice.common.status.ROLE
import com.example.securitypractice.member.dto.LoginDto
import com.example.securitypractice.member.dto.UserDtoRequest
import com.example.securitypractice.member.entity.User
import com.example.securitypractice.member.entity.UserRole
import com.example.securitypractice.member.repository.UserRepository
import com.example.securitypractice.member.repository.UserRoleRepository
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService(
    private val userRepository: UserRepository,
    private val userRoleRepository: UserRoleRepository,
    // 추가
    private val authenticationManagerBuilder: AuthenticationManagerBuilder,
    private val jwtTokenProvider: JwtTokenProvider,
) {

    @Transactional
    fun signUp(userDtoRequest: UserDtoRequest): String {
        // email 중복 검사
        var user: User? = userRepository.findByEmail(userDtoRequest.email)
        if (user != null) {
            throw InvalidInputException("email", "이미 등록된 email입니다.")
        }
        // nickname 중복 검사 추가 ??

        // 사용자 정보 저장
        user = userDtoRequest.toEntity()
        userRepository.save(user)

        // 권한 저장
        val userRole = UserRole(role = ROLE.MEMBER, user = user)
        userRoleRepository.save(userRole)

        return "회원가입이 완료되었습니다."
    }

    @Transactional
    fun login(loginDto: LoginDto): TokenInfo {
        val authenticationToken =
            UsernamePasswordAuthenticationToken(loginDto.email, loginDto.password)
        // 먼저 로그인 요청 정보(LoginDto)로부터 아이디와 비밀번호를 가져와
        // UsernamePasswordAuthenticationToken을 생성한다.
        // 이 토큰은 사용자의 인증 정보를 나타내는 객체이다.

        val authentication =
            authenticationManagerBuilder.`object`.authenticate(authenticationToken)
        // 이 토큰을 AuthenticationManager에 전달하여 인증을 시도한다.
        // AuthenticationManager는 주어진 토큰의 인증 정보를 검증하고, 검증에 성공하면 Authentication 객체를 반환한다.
        // 이 객체는 인증된 사용자의 정보를 포함하고 있다.

        return jwtTokenProvider.createToken(authentication)
        // 마지막으로 인증된 사용자의 정보를 바탕으로 JWT 토큰을 생성하고 반환한다.
        // 이 토큰은 사용자가 서버에 요청을 보낼 때마다 인증 정보를 제공하는 데 사용된다.
    }
}
