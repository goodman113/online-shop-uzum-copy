package project.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import project.model.Vendor;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class VendorUpdateWebSocketHandler extends TextWebSocketHandler {

    private final Set<WebSocketSession> sessions = ConcurrentHashMap.newKeySet();
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        sessions.add(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessions.remove(session);
    }

    // Call this from controller after approve/reject
    public void broadcastUpdate(Vendor vendor, String action) {
        Map<String, Object> payload = Map.of(
            "action", action,
            "shopName", vendor.getShopName(),
            "timestamp", LocalDateTime.now().toString(),
            "vendorId", vendor.getId()
        );

        String json = "";
        try {
            json = mapper.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            return;
        }

        String finalJson = json;
        sessions.forEach(session -> {
            if (session.isOpen()) {
                try {
                    session.sendMessage(new TextMessage(finalJson));
                } catch (IOException e) {
                    // ignore
                }
            }
        });
    }
}