package com.file.repository;

import com.file.Entity.FileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FileRepository extends JpaRepository<FileEntity, Long> {
    List<FileEntity> findByOwnerId(String ownerId);
}
