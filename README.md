# 🚀 Sistema de Notificações Multicanal (Spring Boot 3 + Java 21)

Projeto Spring Boot que envia notificações por **E‑mail**, **WhatsApp**, **SMS** e **Discord** usando padrões de projeto (Strategy, Factory, Facade, Template Method, etc.).

## Como Executar

```powershell
# clonar o repositório e entrar na pasta
git clone <repo-url>
cd notify-hub

# executar com Maven Wrapper
./mvnw.cmd spring-boot:run
```

A aplicação fica disponível em `http://localhost:8080/` com Swagger UI em `/swagger-ui/index.html` e console H2 em `/h2-console`.

## Credenciais

Defina as variáveis de ambiente:

```powershell
$env:MAILTRAP_API_TOKEN = "seu_token"
$env:TWILIO_ACCOUNT_SID = "ACxxxx"
$env:TWILIO_AUTH_TOKEN = "seu_token"
```

Se não forem fornecidas, o app funciona em modo mock.

## Estrutura de Código

- `NotificationController` – ponto de entrada REST
- `NotificationService` – façade que orquestra persistência e estratégia
- `NotificationStrategyFactory` – cria a estratégia adequada para cada canal
- Estratégias (`EmailNotification`, `WhatsAppNotification`, `SmsNotification`, `DiscordNotification`) implementam `NotificationStrategy`

## Testes

```powershell
./mvnw.cmd test
```

Todos os testes passam.

---

*Este README foi simplificado para melhorar a legibilidade.*