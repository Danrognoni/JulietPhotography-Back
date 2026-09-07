package com.julietamarateo.photography.controller;

import com.julietamarateo.photography.dto.ContactMessageDto;
import com.julietamarateo.photography.entity.ContactMessage;
import com.julietamarateo.photography.repository.ContactMessageRepository;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.HtmlUtils;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping({"/api/contact", "/contact"})
public class ContactMessageController {

    private static final Logger log = LoggerFactory.getLogger(ContactMessageController.class);

    private final ContactMessageRepository contactMessageRepository;
    private final RestClient restClient;

    @Value("${brevo.api.key:}")
    private String brevoApiKey;

    @Value("${brevo.api.url:https://api.brevo.com/v3/smtp/email}")
    private String brevoApiUrl;

    @Value("${app.mail.to:julietamarateo4@gmail.com}")
    private String mailTo;

    public ContactMessageController(ContactMessageRepository contactMessageRepository) {
        this(contactMessageRepository, RestClient.builder());
    }

    @Autowired
    public ContactMessageController(ContactMessageRepository contactMessageRepository,
                                    @Autowired(required = false) RestClient.Builder restClientBuilder) {
        this.contactMessageRepository = contactMessageRepository;
        this.restClient = (restClientBuilder != null) ? restClientBuilder.build() : RestClient.create();
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
            String destinationEmail = (mailTo != null && !mailTo.isBlank()) ? mailTo : "julietamarateo4@gmail.com";

            String safeName = HtmlUtils.htmlEscape(dto.getName() != null ? dto.getName() : "");
            String safeEmail = HtmlUtils.htmlEscape(dto.getEmail() != null ? dto.getEmail() : "");
            String safeMessage = HtmlUtils.htmlEscape(dto.getMessage() != null ? dto.getMessage() : "")
                    .replace("\r\n", "<br>")
                    .replace("\n", "<br>");

            String htmlContent = String.format(
                    "<p><strong>Nombre:</strong> %s</p><p><strong>Email:</strong> %s</p><p><strong>Mensaje:</strong><br>%s</p>",
                    safeName,
                    safeEmail,
                    safeMessage
            );

            Map<String, Object> brevoPayload = Map.of(
                    "sender", Map.of("name", "Portfolio Contacto", "email", destinationEmail),
                    "to", List.of(Map.of("email", destinationEmail)),
                    "replyTo", Map.of(
                            "email", dto.getEmail() != null ? dto.getEmail() : "",
                            "name", dto.getName() != null ? dto.getName() : ""
                    ),
                    "subject", "Nuevo mensaje de contacto en el Portfolio",
                    "htmlContent", htmlContent
            );

            return restClient.post()
                    .uri(brevoApiUrl)
                    .header("api-key", brevoApiKey != null ? brevoApiKey : "")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(brevoPayload)
                    .exchange((request, response) -> {
                        int statusCode = response.getStatusCode().value();
                        String responseBody = new String(response.getBody().readAllBytes(), StandardCharsets.UTF_8);

                        if (statusCode == 201) {
                            log.info("Email de contacto despachado exitosamente mediante Brevo API para el mensaje ID #{}", saved.getId());
                            return ResponseEntity.ok(Map.of(
                                    "success", true,
                                    "message", "Mensaje enviado exitosamente",
                                    "id", saved.getId()
                            ));
                        } else {
                            log.error("Fallo en la API de Brevo al enviar email de contacto. Status: {}, Respuesta: {}", statusCode, responseBody);
                            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                                    "success", false,
                                    "message", "Hubo un error al enviar el mensaje. Por favor, intenta de nuevo o comunícate por redes/WhatsApp.",
                                    "error", responseBody.isBlank() ? ("Status code: " + statusCode) : responseBody
                            ));
                        }
                    });
        } catch (Exception ex) {
            log.error("Error crítico al enviar email de contacto mediante API de Brevo: {}", ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "message", "Hubo un error al enviar el mensaje. Por favor, intenta de nuevo o comunícate por redes/WhatsApp.",
                    "error", ex.getMessage() != null ? ex.getMessage() : "Error desconocido al despachar correo vía Brevo API"
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
