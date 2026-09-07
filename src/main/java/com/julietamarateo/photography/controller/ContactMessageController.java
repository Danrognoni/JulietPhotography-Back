package com.julietamarateo.photography.controller;

import com.julietamarateo.photography.dto.ContactMessageDto;
import com.julietamarateo.photography.entity.ContactMessage;
import com.julietamarateo.photography.repository.ContactMessageRepository;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping({"/api/contact", "/contact"})
public class ContactMessageController {

    private static final Logger log = LoggerFactory.getLogger(ContactMessageController.class);

    private final ContactMessageRepository contactMessageRepository;
    private final JavaMailSender mailSender;

    @Value("${spring.mail.username:}")
    private String mailFrom;

    @Value("${app.mail.to:julietamarateo4@gmail.com}")
    private String mailTo;

    public ContactMessageController(ContactMessageRepository contactMessageRepository, JavaMailSender mailSender) {
        this.contactMessageRepository = contactMessageRepository;
        this.mailSender = mailSender;
    }

    /**
     * Endpoint público para enviar un mensaje desde el formulario de contacto.
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> submitMessage(@Valid @RequestBody ContactMessageDto dto) {
        log.info("Recibida solicitud de contacto de: {} ({})", dto.getName(), dto.getEmail());

        ContactMessage message = new ContactMessage(
                dto.getName(),
                dto.getEmail(),
                dto.getSubject(),
                dto.getMessage()
        );
        ContactMessage saved = contactMessageRepository.save(message);

        try {
            SimpleMailMessage mail = new SimpleMailMessage();
            String from = (mailFrom != null && !mailFrom.isBlank()) ? mailFrom : dto.getEmail();
            mail.setFrom(from);
            mail.setReplyTo(dto.getEmail());
            mail.setTo(mailTo);

            String subject = (dto.getSubject() != null && !dto.getSubject().isBlank())
                    ? "Nuevo mensaje de contacto: " + dto.getSubject()
                    : "Nuevo mensaje de contacto desde Portfolio";
            mail.setSubject(subject);

            String body = String.format(
                    "Has recibido un nuevo mensaje desde el formulario de contacto de tu portfolio web:\n\n" +
                    "Nombre: %s\n" +
                    "Email: %s\n" +
                    "Asunto: %s\n\n" +
                    "Mensaje:\n%s\n\n" +
                    "---\nEste mensaje fue registrado en el panel administrativo con ID #%d.",
                    dto.getName(),
                    dto.getEmail(),
                    dto.getSubject() != null ? dto.getSubject() : "Sin asunto",
                    dto.getMessage(),
                    saved.getId()
            );
            mail.setText(body);

            mailSender.send(mail);
            log.info("Email de contacto despachado exitosamente a {} para el mensaje ID #{}", mailTo, saved.getId());

            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "success", true,
                    "message", "Mensaje enviado exitosamente",
                    "id", saved.getId()
            ));
        } catch (Exception ex) {
            log.error("Error crítico al enviar email de contacto a través de SMTP: {}", ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "message", "Hubo un error al enviar el mensaje. Por favor, intenta de nuevo o comunícate por redes/WhatsApp.",
                    "error", ex.getMessage() != null ? ex.getMessage() : "Error desconocido al despachar correo SMTP"
            ));
        }
    }

    /**
     * Endpoint protegido para que el Administrador consulte todos los mensajes.
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ContactMessageDto>> getAllMessages() {
        List<ContactMessageDto> list = contactMessageRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(ContactMessageDto::fromEntity)
                .toList();
        return ResponseEntity.ok(list);
    }

    /**
     * Endpoint protegido para marcar como leído un mensaje.
     */
    @PatchMapping("/{id}/read")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ContactMessageDto> markAsRead(@PathVariable Long id) {
        return contactMessageRepository.findById(id)
                .map(msg -> {
                    msg.setRead(true);
                    return ResponseEntity.ok(ContactMessageDto.fromEntity(contactMessageRepository.save(msg)));
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Endpoint protegido para eliminar un mensaje.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteMessage(@PathVariable Long id) {
        if (contactMessageRepository.existsById(id)) {
            contactMessageRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
