package com.notificacoes.controller.dto;

import com.notificacoes.model.Notification;
import com.notificacoes.model.NotificationChannel;
import com.notificacoes.model.NotificationStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Resposta detalhada com o estado da notificação")
public record NotificationResponse(
        Long id,
        String recipient,
        String subject,
        String message,
        NotificationChannel channel,
        NotificationStatus status,
        LocalDateTime createdAt,
        LocalDateTime sentAt,
        String errorMessage
) {
    public static NotificationResponse fromEntity(Notification notification) {
        return new NotificationResponse(
                notification.getId(),
                notification.getRecipient(),
                notification.getSubject(),
                notification.getMessage(),
                notification.getChannel(),
                notification.getStatus(),
                notification.getCreatedAt(),
                notification.getSentAt(),
                notification.getErrorMessage()
        );
    }
}
