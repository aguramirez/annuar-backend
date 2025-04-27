package com.cinetickets.api.service;

import com.cinetickets.api.dto.response.OrderDetailsResponse;
import com.cinetickets.api.entity.User;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    
    @Value("${spring.mail.username}")
    private String fromEmail;
    
    @Value("${app.email.base-url}")
    private String baseUrl;

    /**
     * Envía un correo electrónico de bienvenida al usuario registrado
     */
    @Async
    public void sendWelcomeEmail(User user) {
        try {
            Map<String, Object> variables = new HashMap<>();
            variables.put("name", user.getFirstName());
            variables.put("baseUrl", baseUrl);
            
            String htmlContent = processTemplate("welcome-email", variables);
            
            sendHtmlEmail(
                    user.getEmail(),
                    "¡Bienvenido a CineTickets!",
                    htmlContent
            );
            
            log.info("Welcome email sent to {}", user.getEmail());
        } catch (Exception e) {
            log.error("Error sending welcome email to {}", user.getEmail(), e);
        }
    }

    /**
     * Envía un correo electrónico con la confirmación de compra y las entradas
     */
    @Async
    public void sendTicketConfirmationEmail(User user, OrderDetailsResponse order) {
        try {
            Map<String, Object> variables = new HashMap<>();
            variables.put("name", user.getFirstName());
            variables.put("orderId", order.getId());
            variables.put("movieTitle", order.getShowDetails().getMovieTitle());
            variables.put("date", order.getShowDetails().getStartTime());
            variables.put("cinema", order.getShowDetails().getCinemaName());
            variables.put("room", order.getShowDetails().getRoomName());
            variables.put("seats", order.getReservedSeats());
            variables.put("qrCode", order.getQrCode());
            variables.put("baseUrl", baseUrl);
            
            String htmlContent = processTemplate("ticket-confirmation", variables);
            
            sendHtmlEmail(
                    user.getEmail(),
                    "Confirmación de Compra - CineTickets",
                    htmlContent
            );
            
            log.info("Ticket confirmation email sent to {}", user.getEmail());
        } catch (Exception e) {
            log.error("Error sending ticket confirmation email to {}", user.getEmail(), e);
        }
    }

    /**
     * Envía un correo electrónico para recuperar la contraseña
     */
    @Async
    public void sendPasswordResetEmail(User user, String token) {
        try {
            Map<String, Object> variables = new HashMap<>();
            variables.put("name", user.getFirstName());
            variables.put("resetLink", baseUrl + "/reset-password?token=" + token);
            variables.put("baseUrl", baseUrl);
            
            String htmlContent = processTemplate("password-reset", variables);
            
            sendHtmlEmail(
                    user.getEmail(),
                    "Recuperación de Contraseña - CineTickets",
                    htmlContent
            );
            
            log.info("Password reset email sent to {}", user.getEmail());
        } catch (Exception e) {
            log.error("Error sending password reset email to {}", user.getEmail(), e);
        }
    }

    /**
     * Envía un correo electrónico con una encuesta post-servicio
     */
    @Async
    public void sendSurveyEmail(User user, UUID orderId) {
        try {
            Map<String, Object> variables = new HashMap<>();
            variables.put("name", user.getFirstName());
            variables.put("surveyLink", baseUrl + "/survey?orderId=" + orderId);
            variables.put("baseUrl", baseUrl);
            
            String htmlContent = processTemplate("survey-email", variables);
            
            sendHtmlEmail(
                    user.getEmail(),
                    "¿Cómo fue tu experiencia? - CineTickets",
                    htmlContent
            );
            
            log.info("Survey email sent to {}", user.getEmail());
        } catch (Exception e) {
            log.error("Error sending survey email to {}", user.getEmail(), e);
        }
    }

    /**
     * Método auxiliar para procesar la plantilla Thymeleaf
     */
    private String processTemplate(String templateName, Map<String, Object> variables) {
        Context context = new Context();
        variables.forEach(context::setVariable);
        return templateEngine.process(templateName, context);
    }

    /**
     * Método auxiliar para enviar un correo electrónico HTML
     */
    private void sendHtmlEmail(String to, String subject, String htmlContent) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        
        helper.setFrom(fromEmail);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlContent, true);
        
        mailSender.send(message);
    }
}