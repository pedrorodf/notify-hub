package com.notificacoes.service.strategy;

import com.notificacoes.model.Notification;
import com.notificacoes.model.NotificationStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

/**
 * Padrão Template Method: Define o fluxo invariante do envio de notificação
 * (validação -> formatação -> despacho -> pós-processamento).
 */
public abstract class AbstractNotificationSender implements NotificationStrategy {

    protected final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public void send(Notification notification) {
        log.info("[Template Method] Iniciando fluxo de envio via {}", getChannel());
        
        // 1. Validação do destinatário de acordo com o canal
        validateRecipient(notification.getRecipient());

        // 2. Formatação da mensagem com cabeçalhos/templates
        String formattedMessage = formatMessage(notification);

        // 3. Despacho real ou simulado do payload
        dispatch(notification, formattedMessage);

        // 4. Pós-processamento de sucesso
        onSuccess(notification);
        log.info("[Template Method] Notificação ID {} enviada com sucesso via {}", notification.getId(), getChannel());
    }

    protected abstract void validateRecipient(String recipient);

    protected String formatMessage(Notification notification) {
        if (notification.getSubject() != null && !notification.getSubject().isBlank()) {
            return String.format("[%s] %s", notification.getSubject(), notification.getMessage());
        }
        return notification.getMessage();
    }

    protected abstract void dispatch(Notification notification, String formattedContent);

    protected void onSuccess(Notification notification) {
        notification.setStatus(NotificationStatus.SENT);
        notification.setSentAt(LocalDateTime.now());
        notification.setErrorMessage(null);
    }
}
