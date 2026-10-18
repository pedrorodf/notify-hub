package com.notificacoes.service.strategy;

import com.notificacoes.model.Notification;
import com.notificacoes.model.NotificationChannel;
import io.mailtrap.client.MailtrapClient;
import io.mailtrap.config.MailtrapConfig;
import io.mailtrap.factory.MailtrapClientFactory;
import io.mailtrap.model.request.emails.Address;
import io.mailtrap.model.request.emails.MailtrapMail;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.regex.Pattern;

@Component
public class EmailNotificationStrategy extends AbstractNotificationSender {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");

    @Value("${mailtrap.api-token:}")
    private String apiToken;

    @Value("${mailtrap.from-email:hello@demomailtrap.co}")
    private String fromEmail;

    @Value("${mailtrap.from-name:Mailtrap Test}")
    private String fromName;

    @Override
    public NotificationChannel getChannel() {
        return NotificationChannel.EMAIL;
    }

    @Override
    protected void validateRecipient(String recipient) {
        if (recipient == null || !EMAIL_PATTERN.matcher(recipient).matches()) {
            throw new IllegalArgumentException("Destinatário de e-mail inválido: " + recipient);
        }
    }

    @Override
    protected void dispatch(Notification notification, String formattedContent) {
        log.info("📧 [EMAIL MAILTRAP SDK] Preparando envio de e-mail para: <{}> | Assunto: [{}]",
                notification.getRecipient(), notification.getSubject());

        if (apiToken == null || apiToken.isBlank() || apiToken.startsWith("COLE_") || apiToken.contains("<YOUR_API_TOKEN>")) {
            log.warn("⚠️ [EMAIL MAILTRAP SDK] Token da API do Mailtrap não configurado. Defina a variável de ambiente MAILTRAP_API_TOKEN ou configure mailtrap.api-token no application.yml.");
            throw new IllegalStateException("Token do Mailtrap não configurado. Defina a variável de ambiente MAILTRAP_API_TOKEN ou configure mailtrap.api-token no application.yml.");
        }

        try {
            MailtrapConfig config = new MailtrapConfig.Builder()
                    .token(apiToken.trim())
                    .build();

            MailtrapClient client = MailtrapClientFactory.createMailtrapClient(config);

            MailtrapMail mail = MailtrapMail.builder()
                    .from(new Address(fromEmail, fromName))
                    .to(List.of(new Address(notification.getRecipient())))
                    .subject(notification.getSubject() != null && !notification.getSubject().isBlank()
                            ? notification.getSubject()
                            : "Notificação Multicanal")
                    .text(formattedContent)
                    .category("Notificações Multicanal")
                    .build();

            Object response = client.send(mail);
            log.info("✅ [EMAIL MAILTRAP SDK] E-mail despachado com sucesso via Mailtrap SDK para: {}. Resposta: {}",
                    notification.getRecipient(), response);
        } catch (Exception ex) {
            log.error("❌ [EMAIL MAILTRAP SDK ERROR] Falha ao enviar e-mail via Mailtrap SDK: {}", ex.getMessage());
            throw new RuntimeException("Erro no SDK do Mailtrap: " + ex.getMessage(), ex);
        }
    }
}
