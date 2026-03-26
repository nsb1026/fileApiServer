package com.file.controller;

import com.example.demo.DemoApplication;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = DemoApplication.class)
@AutoConfigureMockMvc
public class FileApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${internal.api.key}")
    private String internalApiKey;

    @Test
    @DisplayName("1. API KEY 없이 토큰 발급 시도 - 실패(401)")
    void generateTokenWithoutKey() throws Exception {
        Map<String, String> request = new HashMap<>();
        request.put("userId", "testUser");

        mockMvc.perform(post("/api/auth/token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("2. API KEY로 토큰 발급 및 파일 업로드/조회 전체 프로세스 - 성공")
    void fullProcessTest() throws Exception {
        // A. 토큰 발급
        Map<String, String> authRequest = new HashMap<>();
        authRequest.put("userId", "testUser");

        MvcResult authResult = mockMvc.perform(post("/api/auth/token")
                .header("X-API-KEY", internalApiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = authResult.getResponse().getContentAsString();
        String token = objectMapper.readTree(responseBody).get("accessToken").asText();
        String authHeader = "Bearer " + token;

        // B. 파일 업로드
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.txt", "text/plain", "Hello World".getBytes()
        );

        MvcResult uploadResult = mockMvc.perform(multipart("/api/files")
                .file(file)
                .header("Authorization", authHeader))
                .andExpect(status().isOk())
                .andReturn();

        String fileId = uploadResult.getResponse().getContentAsString();

        // C. 파일 단건 조회
        mockMvc.perform(get("/api/files/" + fileId)
                .header("Authorization", authHeader))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.originalName").value("test.txt"))
                .andExpect(jsonPath("$.ownerId").value("testUser"));

        // D. 일괄 조회 (Batch)
        mockMvc.perform(get("/api/files/batch")
                .param("ids", fileId)
                .header("Authorization", authHeader))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(fileId));
    }
}
