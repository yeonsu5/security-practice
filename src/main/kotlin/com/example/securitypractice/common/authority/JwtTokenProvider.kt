package com.example.securitypractice.common.authority

import io.jsonwebtoken.Claims
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.MalformedJwtException
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.UnsupportedJwtException
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component
import java.lang.IllegalArgumentException
import java.lang.RuntimeException
import java.util.*

const val EXPIRATION_MILLISECONDS: Long = 1000 * 60 * 60 * 12

// JwtTokenProvider - 토큰 생성, 토큰 정보 추출, 토큰 검증
@Component
class JwtTokenProvider {
    @Value("\${jwt.secret}")
    lateinit var secretKey: String

    private val key by lazy {
        Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey))
    }

    // 1. 토큰 생성
    fun createToken(authentication: Authentication): TokenInfo {
        val authorities: String = authentication
            .authorities
            .joinToString(",", transform = GrantedAuthority::getAuthority)
        // 인증된 사용자의 권한(authorities)을 콤마(,)로 구분된 문자열로 변환하는 코드
        // authentication.authorities는 인증된 사용자가 가진 모든 권한을 가져온다.
        // 각각의 권한은 GrantedAuthority 인터페이스를 구현한 객체
        // joinToString(",")은 이 권한 목록을 콤마로 구분된 하나의 문자열로 합치는 작업을 함
        // transform = GrantedAuthority::getAuthority는 각 권한 객체에서 실제 권한 문자열을 가져오는 함수를 지정
        // GrantedAuthority::getAuthority는 GrantedAuthority 인터페이스의 메서드로,
        // 권한을 나타내는 문자열을 반환

        // 따라서 이 코드는 인증된 사용자가 가진 모든 권한을 하나의 문자열로 합쳐서
        // authorities라는 변수에 할당. 문자열은 각 권한이 콤마로 구분되어 있음.

        val now = Date()
        val accessExpiration = Date(now.time + EXPIRATION_MILLISECONDS)

        // AccessToken
        val accessToken = Jwts.builder()
            .setSubject(authentication.name) // 토큰의 주제 설정: 인증된 사용자의 이름 사용
            .claim("auth", authorities) // 토큰에 추가적인 정보 담기 : 사용자의 권한을 "auth"라는 이름으로 담기
            .setIssuedAt(now)
            .setExpiration(accessExpiration)
            .signWith(key, SignatureAlgorithm.HS256) // 서명하는 키와 알고리즘
            .compact()

        return TokenInfo("Bearer", accessToken)
    }

    // 2. 토큰 정보 추출
    fun getAuthentication(token: String): Authentication { // 토큰에서 인증 정보를 추출하여 Authentication 객체를 반환
        val claims: Claims = getClaims(token) // 토큰에서 Claims를 추출
        // JWT 토큰의 본문에 해당하는 Claims에는 사용자 정보와 권한 정보 등이 포함되어 있음

        val auth = claims["auth"] ?: throw RuntimeException("잘못된 토큰 입니다.")
        // Claims에서 "auth"라는 이름으로 저장된 권한 정보를 가져오기. 권한 정보가 없다면 예외.

        // 권한 정보 추출
        val authorities: Collection<GrantedAuthority> = (auth as String) // 권한 정보는 콤마로 구분된 문자열 형태이므로
            .split(",") // 이를 ','로 분리하고 각 권한 문자열을
            .map { SimpleGrantedAuthority(it) } // SimpleGrantedAuthority 객체로 변환하여 authorities라는 변수에 저장
        // SimpleGrantedAuthority는 권한을 나타내는 클래스로, GrantedAuthority 인터페이스를 구현하고 있음

        // *** User import 주의
        val principal: UserDetails = User(claims.subject, "", authorities)
        // UserDetails를 구현하는 User 객체를 생성한다.
        // 이때 사용자 이름으로 claims.subject를 사용하고, 비밀번호는 비워둔다.
        // 권한 정보로는 위에서 추출한 authorities를 사용한다.

        return UsernamePasswordAuthenticationToken(principal, "", authorities)
        // 마지막으로 Authentication 객체를 생성하여 반환한다.
        // Authentication 객체는 Spring Security에서 인증 정보를 나타내는 인터페이스

        // 이 함수를 통해 JWT 토큰에서 인증 정보를 추출하고, 이를 Spring Security가 사용할 수 있는 형태로 변환하는
        // 작업을 수행한다.
    }

    // 3. 토큰 검증
    fun validateToken(token: String): Boolean {
        try {
            getClaims(token)
            return true
        } catch (e: Exception) {
            when (e) {
                is SecurityException -> {} // Invalid JWT Token
                is MalformedJwtException -> {} // Invalid JWT Token
                is ExpiredJwtException -> {} // Expired JWT Token
                is UnsupportedJwtException -> {} // Unsupported JWT Token
                is IllegalArgumentException -> {} // JWT claims string is empty
                else -> {} // else
            }
            println(e.message)
        }
        return false
    }

    private fun getClaims(token: String): Claims = // 주어진 JWT 토큰에서 Claims를 추출하는 함수
        Jwts.parserBuilder() // JWT 토큰을 파싱하기 위한 파서를 생성하는 부분 (.build()까지)
            .setSigningKey(key) // 파싱할 때 사용할 서명 키를 설정
            .build()
            .parseClaimsJws(token) // 주어진 JWT 토큰을 파싱하고, 이 결과로 Jws<Claims> 객체를 반환
            // 이 객체는 서명된 JWT 토큰의 헤더와 본문, 서명을 담고 있다.
            .body // 이 Jws 객체에서 본문인 Claims를 가져오는 부분.
    // Claims는 JWT 토큰의 본문에 해당하는 정보를 담고 있으며,
    // 일반적으로 사용자의 식별자, 권한, 토큰의 만료 시간 등의 정보를 담는다.
}
