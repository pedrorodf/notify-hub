package com.notificacoes.service;

import com.notificacoes.controller.dto.NotificationRequest;
import com.notificacoes.controller.dto.NotificationResponse;
import com.notificacoes.model.NotificationStatus;

import java.util.List;

/**
 * Padrão Facade: Interface pública de serviços de notificação,
 * simplificando o uso para os controllers e clientes.
 */
public interface NotificationService {

    NotificationResponse sendNotification(NotificationRequest request);

    List<NotificationResponse> listAll();

    NotificationResponse findById(Long id);

    List<NotificationResponse> findByStatus(NotificationStatus status);

    NotificationResponse retry(Long id);
}
