package com.file.controller;

import com.file.Entity.FileEntity;
import com.file.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    // 업로드
    @PostMapping
    public ResponseEntity<?> upload(
            @RequestParam("file") MultipartFile file,
            Authentication authentication) throws IOException {

        String userId = (String) authentication.getPrincipal();

        FileEntity saved = fileService.upload(file, userId);

        return ResponseEntity.ok(saved.getId());
    }

    // 단건 조회
    @GetMapping("/{id}")
    public ResponseEntity<?> get(
            @PathVariable Long id,
            Authentication authentication) {

        String userId = (String) authentication.getPrincipal();

        FileEntity file = fileService.get(id, userId);

        return ResponseEntity.ok(file);
    }

    // 목록 조회
    @GetMapping
    public ResponseEntity<?> list(Authentication authentication) {

        String userId = (String) authentication.getPrincipal();

        return ResponseEntity.ok(fileService.list(userId));
    }

    // 다운로드
    @GetMapping("/{id}/download")
    public ResponseEntity<Resource> download(
            @PathVariable Long id,
            Authentication authentication) throws IOException {

        String userId = (String) authentication.getPrincipal();

        FileEntity file = fileService.get(id, userId);
        Resource resource = fileService.download(id, userId);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(file.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + file.getOriginalName() + "\"")
                .body(resource);
    }

    // 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(
            @PathVariable Long id,
            Authentication authentication) throws IOException {

        String userId = (String) authentication.getPrincipal();

        fileService.delete(id, userId);

        return ResponseEntity.ok().build();
    }
}