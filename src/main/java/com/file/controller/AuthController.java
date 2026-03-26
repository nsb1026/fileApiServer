package com.file.controller;

import com.file.security.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final TokenProvider tokenProvider;

    /**
     * 타 시스템에서 로그인 후 호출하는 토큰 생성 API
     * 보안을 위해 실제 환경에서는 추가적인 API Key나 내부망 제어가 필요합니다.
     */
    @PostMapping("/token")
    public ResponseEntity<?> generateToken(@RequestBody Map<String, String> request) {
        String userId = request.get("userId");
        if (userId == null || userId.isEmpty()) {
            return ResponseEntity.badRequest().body("userId is required");
        }

        String token = tokenProvider.createToken(userId);
        return ResponseEntity.ok(Map.of("accessToken", token));
    }
}
