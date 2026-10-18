package com.notificacoes.client;

import com.notificacoes.client.dto.DiscordWebhookPayload;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.net.URI;

@FeignClient(name = "discordWebhookClient", url = "https://discord.com/api")
public interface DiscordFeignClient {

    @PostMapping
    void sendNotification(URI webhookUri, @RequestBody DiscordWebhookPayload payload);
}
