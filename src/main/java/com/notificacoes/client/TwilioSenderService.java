package com.notificacoes.client;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.rest.api.v2010.account.MessageCreator;
import com.twilio.type.PhoneNumber;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TwilioSenderService {

    private static final Logger log = LoggerFactory.getLogger(TwilioSenderService.class);

    @Value("${twilio.account-sid:}")
    private String accountSid;

    @Value("${twilio.auth-token:}")
    private String authToken;

    @Value("${twilio.from-sms-number:+17372508034}")
    private String fromSmsNumber;

    @Value("${twilio.from-whatsapp-number:+17372508034}")
    private String fromWhatsAppNumber;

    @Value("${twilio.whatsapp-content-sid:}")
    private String whatsappContentSid;

    private boolean configured = false;

    @PostConstruct
    public void init() {
        if (accountSid != null && !accountSid.isBlank() && !accountSid.startsWith("COLE_")
                && authToken != null && !authToken.isBlank() && !authToken.startsWith("COLE_")) {
            try {
                Twilio.init(accountSid.trim(), authToken.trim());
                configured = true;
                log.info("✅ [TWILIO] SDK inicializado com sucesso para Account SID: {}...", accountSid.substring(0, Math.min(8, accountSid.length())));
            } catch (Exception ex) {
                log.error("❌ [TWILIO] Falha ao inicializar SDK: {}", ex.getMessage());
            }
        } else {
            log.warn("⚠️ [TWILIO] Credenciais não configuradas no application.yml. Operando em modo de validação/simulação.");
        }
    }

    public boolean isConfigured() {
        return configured;
    }

    public String sendSms(String recipient, String content) {
        ensureConfigured();
        String formattedTo = formatPhoneNumber(recipient);
        String formattedFrom = formatPhoneNumber(fromSmsNumber);

        log.info("📱 [TWILIO SMS] Enviando de {} para {}", formattedFrom, formattedTo);
        Message message = Message.creator(
                new PhoneNumber(formattedTo),
                new PhoneNumber(formattedFrom),
                content
        ).create();

        log.info("✅ [TWILIO SMS] SMS enviado! SID: {}, Status: {}", message.getSid(), message.getStatus());
        return message.getSid();
    }

    public String sendWhatsApp(String recipient, String content) {
        ensureConfigured();
        String formattedTo = "whatsapp:" + formatPhoneNumber(recipient);
        String formattedFrom = "whatsapp:" + formatPhoneNumber(fromWhatsAppNumber);

        log.info("💬 [TWILIO WHATSAPP] Enviando de {} para {}", formattedFrom, formattedTo);
        MessageCreator creator = Message.creator(
                new PhoneNumber(formattedTo),
                new PhoneNumber(formattedFrom),
                content
        );

        if (whatsappContentSid != null && !whatsappContentSid.isBlank()) {
            log.info("💬 [TWILIO WHATSAPP] Utilizando ContentSid: {}", whatsappContentSid);
            creator.setContentSid(whatsappContentSid.trim());
        }

        Message message = creator.create();
        log.info("✅ [TWILIO WHATSAPP] WhatsApp enviado! SID: {}, Status: {}", message.getSid(), message.getStatus());
        return message.getSid();
    }

    private void ensureConfigured() {
        if (!configured) {
            throw new IllegalStateException("Twilio não configurado. Defina as variáveis TWILIO_ACCOUNT_SID e TWILIO_AUTH_TOKEN ou preencha o application.yml.");
        }
    }

    private String formatPhoneNumber(String phone) {
        if (phone == null) return "";
        String clean = phone.replace("whatsapp:", "").trim();
        if (!clean.startsWith("+")) {
            clean = "+" + clean;
        }
        return clean;
    }
}
