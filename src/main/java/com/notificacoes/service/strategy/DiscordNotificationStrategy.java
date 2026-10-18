package com.notificacoes.service.strategy;

import com.notificacoes.client.DiscordFeignClient;
import com.notificacoes.client.dto.DiscordWebhookPayload;
import com.notificacoes.model.Notification;
import com.notificacoes.model.NotificationChannel;
import org.springframework.stereotype.Component;

import java.net.URI;

@Component
public class DiscordNotificationStrategy extends AbstractNotificationSender {

    private final DiscordFeignClient discordFeignClient;

    public DiscordNotificationStrategy(DiscordFeignClient discordFeignClient) {
        this.discordFeignClient = discordFeignClient;
    }

    @Override
    public NotificationChannel getChannel() {
        return NotificationChannel.DISCORD_WEBHOOK;
    }

    @Override
    protected void validateRecipient(String recipient) {
        if (recipient == null || (!recipient.startsWith("http://") && !recipient.startsWith("https://") && !recipient.equalsIgnoreCase("mock"))) {
            throw new IllegalArgumentException("Destinatário para Discord Webhook deve ser uma URL válida (ex: https://discord.com/api/webhooks/...) ou 'mock'");
        }
    }

    @Override
    protected String formatMessage(Notification notification) {
        String title = (notification.getSubject() != null && !notification.getSubject().isBlank())
                ? "**" + notification.getSubject() + "**\n"
                : "";
        return title + notification.getMessage();
    }

    @Override
    protected void dispatch(Notification notification, String formattedContent) {
        if ("mock".equalsIgnoreCase(notification.getRecipient())) {
            log.info("🤖 [DISCORD MOCK] Webhook simulado com sucesso:\n{}", formattedContent);
            return;
        }

        try {
            log.info("🤖 [DISCORD OPENFEIGN] Disparando webhook real para: {}", notification.getRecipient());
            discordFeignClient.sendNotification(
                    URI.create(notification.getRecipient()),
                    DiscordWebhookPayload.of(formattedContent)
            );
        } catch (Exception ex) {
            log.error("❌ [DISCORD ERROR] Falha ao despachar webhook: {}", ex.getMessage());
            throw new RuntimeException("Erro ao conectar com Webhook do Discord: " + ex.getMessage(), ex);
        }
    }
}
