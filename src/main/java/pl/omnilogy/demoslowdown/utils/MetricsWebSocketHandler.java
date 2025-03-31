package pl.omnilogy.demoslowdown.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@RequiredArgsConstructor
@Component
public class MetricsWebSocketHandler extends TextWebSocketHandler {
    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final SyntheticSender syntheticSender;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        sessions.put(session.getId(), session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessions.remove(session.getId());
    }

    @Scheduled(fixedRate = 1000)
    public void sendMetricsToAll() {
        if (sessions.isEmpty()) {
            return;
        }

        try {
            long heapSize = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

            Map<String, Object> metrics = Map.of(
                    "heapSize", heapSize,
                    "avgResponseTime", syntheticSender.getAverageResponseTime()
            );

            String payload = objectMapper.writeValueAsString(metrics);

            TextMessage message = new TextMessage(payload);
            sessions.forEach((id, session) -> {
                try {
                    if (session.isOpen()) {
                        session.sendMessage(message);
                    }
                } catch (IOException e) {
                    log.error("Error while sending message to session {}", id, e);
                }
            });
        } catch (Exception e) {
            log.error("Error while sending metrics", e);
        }
    }
}
