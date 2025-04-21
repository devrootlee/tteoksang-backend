package com.example.tteoksang.security;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secretkey}")
    private String SECRET_KEY;

    @Value("${jwt.expirationtime}")
    private long EXPIRATION_TIME;

    private SecretKey secretKey;

    @PostConstruct
    public void init() {
        // 문자열 키 → SecretKey로 변환
        this.secretKey = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
    }

    // jwt 생성
    public String generateJwt(int memberId) {
        Date expirationDate = new Date(System.currentTimeMillis() + EXPIRATION_TIME);

        return Jwts.builder()
                .setSubject(String.valueOf(memberId)) // member id
                .setIssuedAt(new Date()) // 발행시간
                .setExpiration(expirationDate)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    //해당 서버에서 만든 jwt 인지 검증
    public boolean validateJwt(String jwt) {
        try {
            jwt = jwt.replace("Bearer ", "");
            Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(jwt);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    // jwt 에서 memberId 구하기
    public String extractMemberIdAtJwt(String jwt) {
        jwt = jwt.replace("Bearer ", "");
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(jwt)
                .getBody()
                .getSubject();
    }

    public Authentication getAuthentication(String token) {
        // JWT 에서 인증 정보 추출
        String username = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
        return new UsernamePasswordAuthenticationToken(username, null, Collections.emptyList());
    }

    // jwt 유효기간 확인
    public boolean isJwtExpired(String jwt) {
        Date expiration = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(jwt)
                .getBody()
                .getExpiration();

        return expiration.before(new Date());
    }
}
