package com.cinetickets.api.controller;

import com.cinetickets.api.dto.response.FileUploadResponse;
import com.cinetickets.api.service.StorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {

    private final StorageService storageService;

    /**
     * Sube una imagen de película (solo para administradores)
     */
    @PostMapping(value = "/upload/movie", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<FileUploadResponse> uploadMovieImage(@RequestParam("file") MultipartFile file) {
        try {
            String fileUrl = storageService.uploadMovieImage(file);
            return ResponseEntity.ok(new FileUploadResponse(fileUrl, "Movie image uploaded successfully"));
        } catch (IOException e) {
            log.error("Error uploading movie image", e);
            return ResponseEntity.badRequest().body(
                    new FileUploadResponse(null, "Failed to upload movie image: " + e.getMessage()));
        }
    }

    /**
     * Sube una imagen de perfil de usuario
     */
    @PostMapping(value = "/upload/profile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FileUploadResponse> uploadProfileImage(@RequestParam("file") MultipartFile file) {
        try {
            String fileUrl = storageService.uploadProfileImage(file);
            return ResponseEntity.ok(new FileUploadResponse(fileUrl, "Profile image uploaded successfully"));
        } catch (IOException e) {
            log.error("Error uploading profile image", e);
            return ResponseEntity.badRequest().body(
                    new FileUploadResponse(null, "Failed to upload profile image: " + e.getMessage()));
        }
    }

    /**
     * Sube una imagen de producto (solo para administradores)
     */
    @PostMapping(value = "/upload/product", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<FileUploadResponse> uploadProductImage(@RequestParam("file") MultipartFile file) {
        try {
            String fileUrl = storageService.uploadProductImage(file);
            return ResponseEntity.ok(new FileUploadResponse(fileUrl, "Product image uploaded successfully"));
        } catch (IOException e) {
            log.error("Error uploading product image", e);
            return ResponseEntity.badRequest().body(
                    new FileUploadResponse(null, "Failed to upload product image: " + e.getMessage()));
        }
    }

    /**
     * Elimina un archivo (solo para administradores)
     */
    @DeleteMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<FileUploadResponse> deleteFile(@RequestParam("fileUrl") String fileUrl) {
        try {
            storageService.deleteFile(fileUrl);
            return ResponseEntity.ok(new FileUploadResponse(null, "File deleted successfully"));
        } catch (Exception e) {
            log.error("Error deleting file", e);
            return ResponseEntity.badRequest().body(
                    new FileUploadResponse(null, "Failed to delete file: " + e.getMessage()));
        }
    }
}