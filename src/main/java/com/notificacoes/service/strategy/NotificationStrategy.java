package com.notificacoes.service.strategy;

import com.notificacoes.model.Notification;
import com.notificacoes.model.NotificationChannel;

/**
 * Padrão Strategy: Interface base para estratégias de envio por canal.
 */
public interface NotificationStrategy {

    NotificationChannel getChannel();

    void send(Notification notification);
}
