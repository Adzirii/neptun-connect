package com.thesis.chatservice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
@Slf4j
public class FileStorageService {

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

    @Value("${file.max-size:10485760}") // 10MB default
    private long maxFileSize;

    public String storeFile(MultipartFile file) throws IOException {
        // Validate file
        if (file.isEmpty()) {
            throw new IOException("Failed to store empty file");
        }

        if (file.getSize() > maxFileSize) {
            throw new IOException("File size exceeds maximum allowed size");
        }

        // Get original filename
        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());

        // Generate unique filename
        String fileExtension = "";
        int dotIndex = originalFilename.lastIndexOf('.');
        if (dotIndex > 0) {
            fileExtension = originalFilename.substring(dotIndex);
        }

        String uniqueFilename = UUID.randomUUID().toString() + fileExtension;

        // Create upload directory if it doesn't exist
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Copy file to the target location
        Path targetLocation = uploadPath.resolve(uniqueFilename);
        Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

        log.info("Stored file: {} as {}", originalFilename, uniqueFilename);
        return uniqueFilename;
    }

    public Path loadFile(String filename) {
        return Paths.get(uploadDir).resolve(filename);
    }

    public void deleteFile(String filename) {
        try {
            Path filePath = loadFile(filename);
            Files.deleteIfExists(filePath);
            log.info("Deleted file: {}", filename);
        } catch (IOException e) {
            log.error("Failed to delete file: {}", filename, e);
        }
    }

    public String getFileUrl(String filename) {
        return "/api/files/" + filename;
    }
}

