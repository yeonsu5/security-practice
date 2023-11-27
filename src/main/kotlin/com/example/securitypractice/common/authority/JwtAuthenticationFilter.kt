package com.example.securitypractice.common.authority

import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.util.StringUtils
import org.springframework.web.filter.GenericFilterBean

// GenericFilterBean 상속받아, JWT 토큰을 이용한 인증을 처리하는 필터를 정의한 것
// Filter로 Token 정보를 검사하고 SecurityContextHolder에 Authentication을 기록

// doFilter 함수는 모든 요청이 처리되는 과정에서 호출되는 함수
// 여기서는 HTTP 요청에서 JWT 토큰을 추출하고, 이 토큰이 유효한지 검사한 후,
// 유효하다면 이 토큰으로부터 인증 정보를 가져와 SecurityContext에 설정하는 작업을 수행한다.

// 이 클래스는 Spring Security의 필터 체인에 추가되어 요청 처리 과정에 참여하게 된다.
class JwtAuthenticationFilter(
    private val jwtTokenProvider: JwtTokenProvider,
) : GenericFilterBean() {

    override fun doFilter(
        request: ServletRequest?,
        response: ServletResponse?,
        chain: FilterChain?,
    ) {
        val token = resolveToken(request as HttpServletRequest)

        if (token != null && jwtTokenProvider.validateToken(token)) {
            val authentication = jwtTokenProvider.getAuthentication(token)
            SecurityContextHolder.getContext().authentication = authentication
        }

        chain?.doFilter(request, response)
        // 이 부분은 현재의 필터가 요청을 처리한 후, 필터 체인의 다음 필터로 요청과 응답을 넘기는 역할을 함
    }

    private fun resolveToken(request: HttpServletRequest): String? {
        val bearerToken = request.getHeader("Authorization")
        // http 요청에서 "Authorization" 헤더를 찾아 JWT 토큰을 추출하는 함수
        return if (StringUtils.hasText(bearerToken) && // bearerToken이 실제 택스트를 가지고 있는지 검증
            bearerToken.startsWith("Bearer") // "Authorization" 헤더의 값이 Bearer로 시작하는 경우
            // 토큰으로 간주하고
        ) {
            bearerToken.substring(7) // 뒤에 따라오는 부분을 반환
        } else { // 헤더가 없거나 "Bearer"로 시작하지 않는 경우
            null // null을 반환
        }
    }
}
