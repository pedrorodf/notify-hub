package com.notificacoes.controller.dto;

import com.notificacoes.model.NotificationChannel;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "Dados para envio de notificação")
public record NotificationRequest(

        @Schema(description = "Destinatário (e-mail, telefone ou URL de webhook)", example = "usuario@empresa.com")
        @NotBlank(message = "O destinatário é obrigatório")
        String recipient,

        @Schema(description = "Assunto ou título da notificação", example = "Atualização de Pedido #1234")
        String subject,

        @Schema(description = "Mensagem ou corpo da notificação", example = "Seu pagamento foi confirmado com sucesso!")
        @NotBlank(message = "A mensagem é obrigatória")
        @Size(max = 1000, message = "A mensagem não pode exceder 1000 caracteres")
        String message,

        @Schema(description = "Canal de envio", example = "EMAIL")
        @NotNull(message = "O canal de envio é obrigatório")
        NotificationChannel channel
) {
}
