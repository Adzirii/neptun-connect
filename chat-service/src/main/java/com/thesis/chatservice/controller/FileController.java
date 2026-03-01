package com.thesis.chatservice.controller;

import com.thesis.chatservice.service.FileStorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Files", description = "File upload and download APIs")
@SecurityRequirement(name = "Bearer Authentication")
public class FileController {

    private final FileStorageService fileStorageService;

    @Operation(summary = "Upload file", description = "Upload a file and get the file URL")
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, String>> uploadFile(
            @RequestParam("file") MultipartFile file
    ) {
        try {
            String filename = fileStorageService.storeFile(file);
            String fileUrl = fileStorageService.getFileUrl(filename);

            Map<String, String> response = new HashMap<>();
            response.put("filename", filename);
            response.put("originalFilename", file.getOriginalFilename());
            response.put("fileUrl", fileUrl);
            response.put("fileType", file.getContentType());
            response.put("fileSize", String.valueOf(file.getSize()));

            return ResponseEntity.ok(response);
        } catch (IOException e) {
            log.error("Failed to upload file", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(summary = "Download file", description = "Download a file by filename")
    @GetMapping("/{filename:.+}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String filename) {
        try {
            Path filePath = fileStorageService.loadFile(filename);
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                String contentType = "application/octet-stream";

                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .header(HttpHeaders.CONTENT_DISPOSITION,
                                "inline; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            log.error("Failed to download file: {}", filename, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

