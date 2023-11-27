package com.example.securitypractice.common.authority

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.factory.PasswordEncoderFactories
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val jwtTokenProvider: JwtTokenProvider,
) {
    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .httpBasic { it.disable() } // basic auth 끄기
            .csrf { it.disable() } // csrf 끄기
            .sessionManagement {
                it.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            } // JWT를 사용하기 때문에 세션은 사용하지 않기
            .authorizeHttpRequests {
                it.requestMatchers("/api/user/signup").anonymous()
                    .anyRequest().permitAll()
            }
            .addFilterBefore( // .addFilterBefore(A, B): B 필터를 실행하기 전에 A 필터 실행하기
                // (A가 통과하면 B는 실행 안 함)
                JwtAuthenticationFilter(jwtTokenProvider),
                UsernamePasswordAuthenticationFilter::class.java,
            )

        return http.build()
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder =
        PasswordEncoderFactories.createDelegatingPasswordEncoder()
}
