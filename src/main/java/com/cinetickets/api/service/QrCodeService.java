package com.cinetickets.api.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.UUID;

@Slf4j
@Service
public class QrCodeService {
    
    @Value("${app.jwt.secret}")
    private String secret;
    
    private static final int QR_CODE_SIZE = 250;

    /**
     * Genera un código QR para una entrada a partir del orderId y otros datos
     * El QR contiene una cadena firmada para validar su autenticidad
     * 
     * @param orderId ID de la orden
     * @param showId ID de la función
     * @param timestamp Marca de tiempo para limitar validez
     * @return Imagen del código QR como una cadena base64
     */
    public String generateTicketQrCode(UUID orderId, UUID showId, long timestamp) {
        try {
            // Crear contenido del QR con información relevante
            String qrContent = String.format("%s|%s|%d|%s", 
                    orderId.toString(), 
                    showId.toString(), 
                    timestamp,
                    generateSignature(orderId, showId, timestamp));
            
            // Generar QR
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(qrContent, BarcodeFormat.QR_CODE, QR_CODE_SIZE, QR_CODE_SIZE);
            
            // Convertir a imagen
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);
            
            // Convertir a base64
            return "data:image/png;base64," + Base64.getEncoder().encodeToString(outputStream.toByteArray());
            
        } catch (WriterException | IOException e) {
            log.error("Error generating QR code", e);
            throw new RuntimeException("Failed to generate QR code", e);
        }
    }
    
    /**
     * Verifica si un código QR es válido
     * 
     * @param qrContent Contenido del QR a validar
     * @return true si el QR es válido, false en caso contrario
     */
    public boolean validateQrCode(String qrContent) {
        try {
            // Extraer componentes del contenido del QR
            String[] parts = qrContent.split("\\|");
            if (parts.length != 4) {
                return false;
            }
            
            UUID orderId = UUID.fromString(parts[0]);
            UUID showId = UUID.fromString(parts[1]);
            long timestamp = Long.parseLong(parts[2]);
            String signature = parts[3];
            
            // Verificar firma
            String expectedSignature = generateSignature(orderId, showId, timestamp);
            if (!expectedSignature.equals(signature)) {
                return false;
            }
            
            // Opcional: validar timestamp si se desea limitar la validez temporal
            // long currentTime = System.currentTimeMillis();
            // if (currentTime - timestamp > MAX_VALIDITY_PERIOD) {
            //     return false;
            // }
            
            return true;
            
        } catch (Exception e) {
            log.error("Error validating QR code", e);
            return false;
        }
    }
    
    /**
     * Genera una firma para el contenido del QR como una medida de seguridad
     */
    private String generateSignature(UUID orderId, UUID showId, long timestamp) {
        // En una implementación real, esto debería usar un algoritmo HMAC real
        String content = orderId + "|" + showId + "|" + timestamp + "|" + secret;
        return Integer.toHexString(content.hashCode());
    }
}