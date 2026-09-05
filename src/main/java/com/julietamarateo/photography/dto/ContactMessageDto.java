package com.julietamarateo.photography.dto;

import com.julietamarateo.photography.entity.ContactMessage;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

public class ContactMessageDto {

    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    private String name;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El formato de email no es válido")
    private String email;

    private String subject;

    @NotBlank(message = "El mensaje es obligatorio")
    private String message;

    private boolean isRead;
    private LocalDateTime createdAt;

    public ContactMessageDto() {
    }

    public static ContactMessageDto fromEntity(ContactMessage entity) {
        if (entity == null) return null;
        ContactMessageDto dto = new ContactMessageDto();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setEmail(entity.getEmail());
        dto.setSubject(entity.getSubject());
        dto.setMessage(entity.getMessage());
        dto.setRead(entity.isRead());
        dto.setCreatedAt(entity.getCreatedAt());
        return dto;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { isRead = read; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
