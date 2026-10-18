package com.notificacoes.client.dto;

public record DiscordWebhookPayload(
        String username,
        String content
) {
    public static DiscordWebhookPayload of(String message) {
        return new DiscordWebhookPayload("Notificador Bot", message);
    }
}
