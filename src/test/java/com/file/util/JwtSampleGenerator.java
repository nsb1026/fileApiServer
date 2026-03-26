package com.file.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * 타 시스템(인증 서버 등)에서 로그인 성공 후 토큰을 생성하는 샘플 코드입니다.
 */
public class JwtSampleGenerator {

    // TokenProvider.java와 동일한 비밀키를 사용해야 합니다.
    private static final String SECRET = "vmbS7KP9860b615da08e1a8e1e723e7f45c26428e535e5d48866a1a1f0c2394c";
    private static final SecretKey KEY = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));

    public static void main(String[] args) {
        String userId = "user123"; // 로그인한 사용자의 고유 ID
        String token = generateToken(userId);

        System.out.println("Generated Token for " + userId + ":");
        System.out.println("Bearer " + token);
    }

    public static String generateToken(String userId) {
        long expirationTime = 1000 * 60 * 60 * 24; // 24시간 유효

        return Jwts.builder()
                .setSubject(userId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(KEY, SignatureAlgorithm.HS256)
                .compact();
    }
}
