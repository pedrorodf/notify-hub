🚀 Sistema de Notificações Multicanal (Spring Boot 3 + Java 21)

Microsserviço corporativo de notificações multicanal desenvolvido com Java 21 LTS e Spring Boot 3.3.4, aplicando na prática padrões de projeto de software (GoF e Spring Framework) para orquestrar e despachar mensagens via múltiplos canais de comunicação (E-mail, WhatsApp, SMS e Discord Webhook).

🏛️ Padrões de Projeto (Design Patterns)

O projeto foi modelado para garantir baixo acoplamento, alta coesão e facilidade de extensão (Princípios SOLID, especialmente Open/Closed Principle):

NotificationController
NotificationService(Facade Pattern)
NotificationRepository(Spring Data JPA / H2)
NotificationStrategyFactory(Factory / Registry)
AbstractNotificationSender(Template Method Pattern)
EmailNotificationStrategy(Mailtrap)
WhatsAppNotificationStrategy(Twilio)
SmsNotificationStrategy(Twilio)
DiscordNotificationStrategy(OpenFeign)
Strategy (NotificationStrategy)
Cada canal de envio isola sua regra de despacho em uma classe especialista independente.
Template Method (AbstractNotificationSender)
Define o esqueleto invariante do fluxo de envio:
Validação → Formatação → Despacho → Pós-processamento e Auditoria
Factory / Registry (NotificationStrategyFactory)
Localiza dinamicamente e injeta o bean correspondente ao canal requisitado, sem cadeias de if/else.
Facade (NotificationService / NotificationServiceImpl)
Ponto único de entrada para orquestrar persistência (PENDING), seleção de estratégia, controle de status (SENT, FAILED) e retentativas (retry).
OpenFeign Declarativo (DiscordFeignClient)
Comunicação HTTP elegante e sem boilerplate para integrações externas.
Singleton & Injeção de Dependências
Ciclo de vida gerenciado nativamente pelo Spring IoC Container.
📱 Canais e Integrações Reais
Canal	SDK / Tecnologia Utilizada	Modo de Operação
📧 E-mail	Mailtrap Java SDK (io.mailtrap:mailtrap-java:1.3.0)	Envio real via API REST do Mailtrap com suporte a logs em tempo real.
💬 WhatsApp	Twilio Java SDK (com.twilio.sdk:twilio:10.6.0)	Despacho via Twilio Sandbox WhatsApp para números pareados com o comando join.
📱 SMS	Twilio Java SDK (com.twilio.sdk:twilio:10.6.0)	Envio de mensagens de texto com sanitização de números para o padrão E.164.
🤖 Discord	Spring Cloud OpenFeign	Disparo de alertas em tempo real para Webhooks de servidores Discord.
💻 Interface Web e Recursos Visuais

A aplicação conta com uma interface moderna em Bootstrap 5.3 acessível diretamente pela raiz http://localhost:8080/:

📝 Formulário Reativo: ajusta rótulos e placeholders dinamicamente de acordo com o canal escolhido.
📋 Tabela de Histórico em Tempo Real: listagem das notificações gravadas, exibindo badges de status (Enviado, Falhou, Pendente) e botão de reenvio (retry).
🌙 Tema Escuro (Dark Mode): alternância instantânea com persistência da preferência no localStorage.
🌐 Swagger UI / OpenAPI 3: documentação interativa em /swagger-ui/index.html.
🗄️ Console do Banco H2: gerenciador visual do banco relacional em /h2-console.
🔑 Configuração de Credenciais (Variáveis de Ambiente)

Para garantir segurança e não versionar segredos no código, todas as credenciais sensíveis utilizam variáveis de ambiente com valores padrão configurados no arquivo src/main/resources/application.yml:

yaml
mailtrap:
  api-token: ${MAILTRAP_API_TOKEN:}
  from-email: ${MAILTRAP_FROM_EMAIL:hello@demomailtrap.co}
  from-name: ${MAILTRAP_FROM_NAME:Mailtrap Test}

twilio:
  account-sid: ${TWILIO_ACCOUNT_SID:}
  auth-token: ${TWILIO_AUTH_TOKEN:}
  from-sms-number: ${TWILIO_FROM_SMS_NUMBER:}
  from-whatsapp-number: ${TWILIO_FROM_WHATSAPP_NUMBER:+14155238886}
  whatsapp-content-sid: ${TWILIO_WHATSAPP_CONTENT_SID:}
Como definir as variáveis no PowerShell (Windows)
powershell
# Credenciais do Mailtrap
$env:MAILTRAP_API_TOKEN="seu_token_mailtrap"

# Credenciais da Twilio
$env:TWILIO_ACCOUNT_SID="ACxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx"
$env:TWILIO_AUTH_TOKEN="sua_auth_token_twilio"
$env:TWILIO_FROM_SMS_NUMBER="+17372508034"

Nota: caso nenhuma credencial seja fornecida, o sistema opera de forma resiliente em modo mock/simulado, validando o fluxo, formatando a mensagem e gravando com sucesso no banco H2 sem travar a aplicação.

▶️ Como Executar o Projeto
Pré-requisitos
Java 21 LTS instalado (java -version).
Maven Wrapper já incluído no repositório (mvnw.cmd / mvnw).
Executando
powershell
# 1. Entrar na pasta do projeto
cd "sistema-notificacoes-multicanal"

# 2. Executar a aplicação com o Maven Wrapper
.\mvnw.cmd spring-boot:run

Após a mensagem Started Application in ... seconds, acesse:

🌐 Painel Web (com Dark Mode): http://localhost:8080/
📄 Swagger UI: http://localhost:8080/swagger-ui/index.html
🗄️ Console Banco H2: http://localhost:8080/h2-console
JDBC URL: jdbc:h2:mem:notificacoesdb
Usuário: sa
Senha: (deixar em branco)
📬 Exemplos de Requisição da API
Envio de Notificação: POST /api/notifications
1. E-mail (Mailtrap SDK)
json
{
  "channel": "EMAIL",
  "recipient": "seu-email@gmail.com",
  "subject": "Confirmação de Pedido #1234",
  "message": "Seu pagamento foi confirmado com sucesso!"
}
2. WhatsApp (Twilio SDK)
json
{
  "channel": "WHATSAPP",
  "recipient": "5511999998888",
  "subject": "Código de Acesso",
  "message": "Seu código de segurança é: 492018"
}
3. Discord Webhook (OpenFeign)
json
{
  "channel": "DISCORD_WEBHOOK",
  "recipient": "mock",
  "subject": "Alerta de Monitoramento",
  "message": "Deploy da versão 1.0.0 concluído com sucesso."
}

(Para disparo real no Discord, substitua "mock" pela URL copiada nas configurações do seu canal do Discord.)

🧪 Testes Automatizados

O projeto conta com testes unitários e de integração utilizando o JUnit 5 e Spring Boot Starter Test:

powershell
.\mvnw.cmd test
Resultado: BUILD SUCCESS com 0 falhas e 0 erros.
