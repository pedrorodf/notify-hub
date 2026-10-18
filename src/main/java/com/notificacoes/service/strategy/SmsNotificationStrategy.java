package com.notificacoes.service.strategy;

import com.notificacoes.client.TwilioSenderService;
import com.notificacoes.model.Notification;
import com.notificacoes.model.NotificationChannel;
import org.springframework.stereotype.Component;

@Component
public class SmsNotificationStrategy extends AbstractNotificationSender {

    private final TwilioSenderService twilioSenderService;

    public SmsNotificationStrategy(TwilioSenderService twilioSenderService) {
        this.twilioSenderService = twilioSenderService;
    }

    @Override
    public NotificationChannel getChannel() {
        return NotificationChannel.SMS;
    }

    @Override
    protected void validateRecipient(String recipient) {
        String digits = recipient != null ? recipient.replaceAll("[^0-9]", "") : "";
        if (digits.length() < 10) {
            throw new IllegalArgumentException("Número de telefone/SMS inválido: " + recipient);
        }
    }

    @Override
    protected void dispatch(Notification notification, String formattedContent) {
        if (!twilioSenderService.isConfigured()) {
            log.warn("⚠️ [SMS SIMULADO] Twilio não configurado no application.yml. Disparando em modo mock para: {}", notification.getRecipient());
            log.info("📱 [SMS MOCK] Conteúdo: \"{}\"", formattedContent);
            return;
        }

        try {
            String sid = twilioSenderService.sendSms(notification.getRecipient(), formattedContent);
            log.info("✅ [SMS REAL] SMS despachado com sucesso via Twilio! SID: {}", sid);
        } catch (Exception ex) {
            log.error("❌ [SMS ERROR] Falha no envio via Twilio: {}", ex.getMessage());
            throw new RuntimeException("Erro ao enviar SMS via Twilio: " + ex.getMessage(), ex);
        }
    }
}
