package com.notificacoes.service.strategy;

import com.notificacoes.client.TwilioSenderService;
import com.notificacoes.model.Notification;
import com.notificacoes.model.NotificationChannel;
import org.springframework.stereotype.Component;

@Component
public class WhatsAppNotificationStrategy extends AbstractNotificationSender {

    private final TwilioSenderService twilioSenderService;

    public WhatsAppNotificationStrategy(TwilioSenderService twilioSenderService) {
        this.twilioSenderService = twilioSenderService;
    }

    @Override
    public NotificationChannel getChannel() {
        return NotificationChannel.WHATSAPP;
    }

    @Override
    protected void validateRecipient(String recipient) {
        String digits = recipient != null ? recipient.replaceAll("[^0-9]", "") : "";
        if (digits.length() < 10) {
            throw new IllegalArgumentException("Número de WhatsApp inválido (necessário DDI+DDD+número, ex: 5511999998888): " + recipient);
        }
    }

    @Override
    protected String formatMessage(Notification notification) {
        // WhatsApp usa markdown com negrito *exemplo*
        String title = notification.getSubject() != null && !notification.getSubject().isBlank()
                ? "*" + notification.getSubject() + "*\n\n"
                : "";
        return title + notification.getMessage();
    }

    @Override
    protected void dispatch(Notification notification, String formattedContent) {
        if (!twilioSenderService.isConfigured()) {
            log.warn("⚠️ [WHATSAPP SIMULADO] Twilio não configurado no application.yml. Disparando em modo mock para: {}", notification.getRecipient());
            log.info("💬 [WHATSAPP MOCK] Conteúdo:\n{}", formattedContent);
            return;
        }

        try {
            String sid = twilioSenderService.sendWhatsApp(notification.getRecipient(), formattedContent);
            log.info("✅ [WHATSAPP REAL] Mensagem despachada com sucesso via Twilio! SID: {}", sid);
        } catch (Exception ex) {
            log.error("❌ [WHATSAPP ERROR] Falha no envio via Twilio: {}", ex.getMessage());
            throw new RuntimeException("Erro ao enviar WhatsApp via Twilio: " + ex.getMessage(), ex);
        }
    }
}
