package com.file.service;

import com.file.Entity.FileEntity;
import com.file.repository.FileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileService {

    private final FileRepository fileRepository;

    private final String uploadDir = "uploads/";

    public FileEntity upload(MultipartFile file, String userId) throws IOException {

        String savedName = UUID.randomUUID() + "_" + file.getOriginalFilename();

        Path path = Paths.get(uploadDir + savedName);
        Files.createDirectories(path.getParent());
        Files.write(path, file.getBytes());

        FileEntity entity = FileEntity.builder()
                .originalName(file.getOriginalFilename())
                .savedName(savedName)
                .path(path.toString())
                .contentType(file.getContentType())
                .size(file.getSize())
                .ownerId(userId)
                .createdAt(LocalDateTime.now())
                .build();

        return fileRepository.save(entity);
    }

    public FileEntity get(Long id, String userId) {
        FileEntity file = fileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("File not found"));

        validateOwner(file, userId);
        return file;
    }

    public List<FileEntity> list(String userId) {
        return fileRepository.findByOwnerId(userId);
    }

    public Resource download(Long id, String userId) throws IOException {
        FileEntity file = get(id, userId);

        Path path = Paths.get(file.getPath());
        return new UrlResource(path.toUri());
    }

    public void delete(Long id, String userId) throws IOException {
        FileEntity file = get(id, userId);

        Files.deleteIfExists(Paths.get(file.getPath()));
        fileRepository.delete(file);
    }

    private void validateOwner(FileEntity file, String userId) {
        if (!file.getOwnerId().equals(userId)) {
            throw new RuntimeException("No permission");
        }
    }
}
