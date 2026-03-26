package com.file.controller;

import com.file.security.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final TokenProvider tokenProvider;

    @Value("${internal.api.key}")
    private String internalApiKey;

    /**
     * 타 시스템에서 로그인 후 호출하는 토큰 생성 API
     * X-API-KEY 헤더를 통해 사전에 약속된 키를 전달해야 토큰이 발급됩니다.
     * curl -X POST http://localhost:8080/api/auth/token \
     *      -H "Content-Type: application/json" \
     *      -H "X-API-KEY: my-secure-internal-api-key-12345" \
     *      -d '{"userId": "user123"}'
     */
    @PostMapping("/token")
    public ResponseEntity<?> generateToken(
            @RequestHeader(value = "X-API-KEY", required = false) String apiKey,
            @RequestBody Map<String, String> request) {

        // API Key 검증
        if (apiKey == null || !apiKey.equals(internalApiKey)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or missing X-API-KEY");
        }

        String userId = request.get("userId");
        if (userId == null || userId.isEmpty()) {
            return ResponseEntity.badRequest().body("userId is required");
        }

        String token = tokenProvider.createToken(userId);
        return ResponseEntity.ok(Map.of("accessToken", token));
    }
}
