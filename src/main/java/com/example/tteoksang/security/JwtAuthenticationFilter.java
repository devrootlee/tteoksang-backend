package com.example.tteoksang.security;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // Authorization 헤더에서 jwt 추출
        String header = request.getHeader("Authorization");
        String jwt = null;
        String memberId = null;

        if (header != null && header.startsWith("Bearer ")) {
            jwt = header.substring(7);
            try {
                // jwt 유효성 검증 및 memberId 추출
                if (jwtUtil.validateJwt(jwt)) {
                    memberId = jwtUtil.extractMemberIdAtJwt(jwt);
                }
            } catch (JwtException | IllegalArgumentException e) {
                // 유효하지 않은 jwt 처리
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
        }

        // memberId 가 존재하고 SecurityContext 에 인증정보가 없는 경우
        if (memberId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // UserDetailService 를 사용하여 사용자 정보 로드
            UserDetails userDetails = userDetailsService.loadUserByUsername(memberId);

            // jwt 가 만료되지 않았는지 확인
            if (! jwtUtil.isJwtExpired(jwt)) {
                // 인증 객체 생성
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // SecurityContext 에 인증 정보 설정
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        // 다음 필터로 요청 전달
        filterChain.doFilter(request, response);
    }
}
