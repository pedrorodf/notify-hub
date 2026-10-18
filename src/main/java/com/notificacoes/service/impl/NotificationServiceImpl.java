package com.notificacoes.service.impl;

import com.notificacoes.controller.dto.NotificationRequest;
import com.notificacoes.controller.dto.NotificationResponse;
import com.notificacoes.model.Notification;
import com.notificacoes.model.NotificationRepository;
import com.notificacoes.model.NotificationStatus;
import com.notificacoes.service.NotificationService;
import com.notificacoes.service.strategy.NotificationStrategy;
import com.notificacoes.service.strategy.NotificationStrategyFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * Implementação da Facade de Notificações, coordenando repositórios,
 * fábrica de estratégias e ciclo de vida do envio.
 */
@Service
public class NotificationServiceImpl implements NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationServiceImpl.class);

    private final NotificationRepository notificationRepository;
    private final NotificationStrategyFactory strategyFactory;

    public NotificationServiceImpl(NotificationRepository notificationRepository,
                                   NotificationStrategyFactory strategyFactory) {
        this.notificationRepository = notificationRepository;
        this.strategyFactory = strategyFactory;
    }

    @Override
    @Transactional
    public NotificationResponse sendNotification(NotificationRequest request) {
        // 1. Cria e persiste com status PENDING
        Notification notification = new Notification(
                request.recipient(),
                request.subject(),
                request.message(),
                request.channel()
        );
        notification = notificationRepository.save(notification);

        // 2. Obtém a estratégia adequada (Factory Pattern) e executa o envio (Strategy Pattern)
        dispatchNotification(notification);

        // 3. Salva o resultado final atualizado
        notification = notificationRepository.save(notification);
        return NotificationResponse.fromEntity(notification);
    }

    @Override
    public List<NotificationResponse> listAll() {
        return notificationRepository.findAll().stream()
                .map(NotificationResponse::fromEntity)
                .toList();
    }

    @Override
    public NotificationResponse findById(Long id) {
        return notificationRepository.findById(id)
                .map(NotificationResponse::fromEntity)
                .orElseThrow(() -> new NoSuchElementException("Notificação não encontrada com ID: " + id));
    }

    @Override
    public List<NotificationResponse> findByStatus(NotificationStatus status) {
        return notificationRepository.findByStatus(status).stream()
                .map(NotificationResponse::fromEntity)
                .toList();
    }

    @Override
    @Transactional
    public NotificationResponse retry(Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Notificação não encontrada com ID: " + id));

        log.info("🔄 Tentando reenviar notificação ID {}", id);
        notification.setStatus(NotificationStatus.PENDING);
        notification.setErrorMessage(null);

        dispatchNotification(notification);

        notification = notificationRepository.save(notification);
        return NotificationResponse.fromEntity(notification);
    }

    private void dispatchNotification(Notification notification) {
        try {
            NotificationStrategy strategy = strategyFactory.getStrategy(notification.getChannel());
            strategy.send(notification);
        } catch (Exception ex) {
            log.error("❌ Falha no envio da notificação ID {}: {}", notification.getId(), ex.getMessage());
            notification.setStatus(NotificationStatus.FAILED);
            notification.setErrorMessage(ex.getMessage());
        }
    }
}
