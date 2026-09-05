package com.julietamarateo.photography.controller;

import com.julietamarateo.photography.dto.ContactMessageDto;
import com.julietamarateo.photography.entity.ContactMessage;
import com.julietamarateo.photography.repository.ContactMessageRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/contact")
public class ContactMessageController {

    private final ContactMessageRepository contactMessageRepository;

    public ContactMessageController(ContactMessageRepository contactMessageRepository) {
        this.contactMessageRepository = contactMessageRepository;
    }

    /**
     * Endpoint público para enviar un mensaje desde el formulario de contacto.
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> submitMessage(@Valid @RequestBody ContactMessageDto dto) {
        ContactMessage message = new ContactMessage(
                dto.getName(),
                dto.getEmail(),
                dto.getSubject(),
                dto.getMessage()
        );
        ContactMessage saved = contactMessageRepository.save(message);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "success", true,
                "message", "Mensaje enviado exitosamente",
                "id", saved.getId()
        ));
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
