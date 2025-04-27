package com.cinetickets.api.service.mock;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * Implementación del servicio de almacenamiento que guarda archivos localmente.
 * Esta clase se activa solo con el perfil "dev" o "test".
 */
@Slf4j
@Service
@Profile({"dev", "test"})
public class LocalStorageService {

    @Value("${app.storage.location:uploads}")
    private String storageLocation;

    /**
     * Sube un archivo al almacenamiento local
     */
    public String uploadFile(MultipartFile file, String folder) throws IOException {
        try {
            String filename = generateFileName(file);
            String directoryPath = storageLocation + "/" + folder;
            String filePath = directoryPath + "/" + filename;
            
            // Crear directorio si no existe
            Path directory = Paths.get(directoryPath);
            if (!Files.exists(directory)) {
                Files.createDirectories(directory);
            }
            
            // Guardar archivo
            Path path = Paths.get(filePath);
            Files.copy(file.getInputStream(), path);
            
            // Devolver URL para acceso (en este caso, una ruta relativa)
            String fileUrl = "/api/files/" + folder + "/" + filename;
            log.info("File uploaded successfully to local storage: {}", fileUrl);
            
            return fileUrl;
        } catch (Exception e) {
            log.error("Error uploading file to local storage", e);
            throw new IOException("Failed to upload file to local storage", e);
        }
    }

    /**
     * Elimina un archivo del almacenamiento local
     */
    public void deleteFile(String fileUrl) {
        try {
            // Extraer ruta del archivo de la URL
            String relativePath = fileUrl.replace("/api/files/", "");
            String filePath = storageLocation + "/" + relativePath;
            
            File file = new File(filePath);
            if (file.exists()) {
                file.delete();
                log.info("File deleted successfully from local storage: {}", fileUrl);
            } else {
                log.warn("File not found for deletion: {}", fileUrl);
            }
        } catch (Exception e) {
            log.error("Error deleting file from local storage", e);
        }
    }

    /**
     * Sube una imagen de película
     */
    public String uploadMovieImage(MultipartFile file) throws IOException {
        return uploadFile(file, "movies");
    }

    /**
     * Sube una imagen de perfil de usuario
     */
    public String uploadProfileImage(MultipartFile file) throws IOException {
        return uploadFile(file, "profiles");
    }

    /**
     * Sube una imagen de producto o combo
     */
    public String uploadProductImage(MultipartFile file) throws IOException {
        return uploadFile(file, "products");
    }

    /**
     * Genera un nombre único para el archivo
     */
    private String generateFileName(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        return UUID.randomUUID().toString() + extension;
    }
}