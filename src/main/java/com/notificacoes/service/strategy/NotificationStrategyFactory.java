package com.notificacoes.service.strategy;

import com.notificacoes.model.NotificationChannel;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Padrão Factory: Localiza e fornece a estratégia correspondente para cada canal.
 */
@Component
public class NotificationStrategyFactory {

    private final Map<NotificationChannel, NotificationStrategy> strategies = new EnumMap<>(NotificationChannel.class);

    public NotificationStrategyFactory(List<NotificationStrategy> strategyList) {
        for (NotificationStrategy strategy : strategyList) {
            strategies.put(strategy.getChannel(), strategy);
        }
    }

    public NotificationStrategy getStrategy(NotificationChannel channel) {
        NotificationStrategy strategy = strategies.get(channel);
        if (strategy == null) {
            throw new IllegalArgumentException("Nenhuma estratégia encontrada para o canal: " + channel);
        }
        return strategy;
    }
}
