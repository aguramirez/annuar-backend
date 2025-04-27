package com.cinetickets.api.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Controlador para servir archivos estáticos en entorno de desarrollo
 */
@Controller
@RequestMapping("/api/files")
@Profile({"dev", "test"})
public class LocalFileController {

    @Value("${app.storage.location:uploads}")
    private String storageLocation;

    /**
     * Sirve archivos subidos de películas
     */
    @GetMapping("/movies/{filename:.+}")
    public ResponseEntity<Resource> serveMovieFile(@PathVariable String filename) {
        return serveFile("movies", filename);
    }

    /**
     * Sirve archivos subidos de perfiles
     */
    @GetMapping("/profiles/{filename:.+}")
    public ResponseEntity<Resource> serveProfileFile(@PathVariable String filename) {
        return serveFile("profiles", filename);
    }

    /**
     * Sirve archivos subidos de productos
     */
    @GetMapping("/products/{filename:.+}")
    public ResponseEntity<Resource> serveProductFile(@PathVariable String filename) {
        return serveFile("products", filename);
    }

    /**
     * Método general para servir archivos
     */
    private ResponseEntity<Resource> serveFile(String folder, String filename) {
        try {
            Path filePath = Paths.get(storageLocation).resolve(folder).resolve(filename);
            Resource resource = new UrlResource(filePath.toUri());
            
            if (resource.exists() && resource.isReadable()) {
                String contentType = determineContentType(filename);
                
                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (MalformedURLException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Determina el tipo de contenido basado en la extensión del archivo
     */
    private String determineContentType(String filename) {
        if (filename.endsWith(".jpg") || filename.endsWith(".jpeg")) {
            return "image/jpeg";
        } else if (filename.endsWith(".png")) {
            return "image/png";
        } else if (filename.endsWith(".gif")) {
            return "image/gif";
        } else if (filename.endsWith(".pdf")) {
            return "application/pdf";
        } else {
            return "application/octet-stream";
        }
    }
}