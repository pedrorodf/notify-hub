package com.notificacoes.controller;

import com.notificacoes.controller.dto.NotificationRequest;
import com.notificacoes.controller.dto.NotificationResponse;
import com.notificacoes.model.NotificationStatus;
import com.notificacoes.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@Tag(name = "Notificações", description = "Endpoints para disparo e consulta de notificações multicanal")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping
    @Operation(summary = "Enviar uma nova notificação", description = "Processa o envio por E-mail, SMS, WhatsApp ou Discord Webhook usando Strategy & Template Method")
    public ResponseEntity<NotificationResponse> send(@Valid @RequestBody NotificationRequest request) {
        NotificationResponse response = notificationService.sendNotification(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "Listar notificações", description = "Retorna todas as notificações, podendo filtrar por status (PENDING, SENT, FAILED)")
    public ResponseEntity<List<NotificationResponse>> listAll(
            @RequestParam(required = false) NotificationStatus status) {
        if (status != null) {
            return ResponseEntity.ok(notificationService.findByStatus(status));
        }
        return ResponseEntity.ok(notificationService.listAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar notificação por ID", description = "Retorna detalhes e status do envio de uma notificação específica")
    public ResponseEntity<NotificationResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(notificationService.findById(id));
    }

    @PostMapping("/{id}/retry")
    @Operation(summary = "Tentar reenviar notificação", description = "Executa uma nova tentativa de envio para notificações que falharam")
    public ResponseEntity<NotificationResponse> retry(@PathVariable Long id) {
        return ResponseEntity.ok(notificationService.retry(id));
    }
}
