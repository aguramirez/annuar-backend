package com.cinetickets.api.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetUrlRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.net.URL;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class StorageService {

    private final S3Client s3Client;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    /**
     * Sube un archivo al almacenamiento S3
     * @param file Archivo a subir
     * @param folder Carpeta dentro del bucket
     * @return URL del archivo subido
     */
    public String uploadFile(MultipartFile file, String folder) throws IOException {
        try {
            String filename = generateFileName(file);
            String key = folder + "/" + filename;

            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(putObjectRequest, 
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

            URL url = s3Client.utilities().getUrl(GetUrlRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build());
                    
            log.info("File uploaded successfully: {}", url);
            return url.toString();
        } catch (Exception e) {
            log.error("Error uploading file to S3", e);
            throw new IOException("Failed to upload file to S3", e);
        }
    }

    /**
     * Elimina un archivo del almacenamiento S3
     * @param fileUrl URL del archivo a eliminar
     */
    public void deleteFile(String fileUrl) {
        try {
            String key = extractKeyFromUrl(fileUrl);
            
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();

            s3Client.deleteObject(deleteObjectRequest);
            log.info("File deleted successfully: {}", fileUrl);
        } catch (Exception e) {
            log.error("Error deleting file from S3", e);
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

    /**
     * Extrae la clave (key) de una URL de S3
     */
    private String extractKeyFromUrl(String url) {
        // Ejemplo de URL: https://bucket-name.s3.region.amazonaws.com/folder/filename.ext
        String domain = "https://" + bucketName + ".s3." + s3Client.serviceClientConfiguration().region() + ".amazonaws.com/";
        return url.replace(domain, "");
    }
}