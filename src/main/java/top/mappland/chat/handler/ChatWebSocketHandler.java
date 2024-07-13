package top.mappland.chat.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import top.mappland.chat.model.dto.GroupMessageDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.mappland.chat.service.GroupService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ChatWebSocketHandler extends TextWebSocketHandler {

    private static final Logger logger = LoggerFactory.getLogger(ChatWebSocketHandler.class);
    private ConcurrentMap<String, ConcurrentMap<String, WebSocketSession>> groupSessions = new ConcurrentHashMap<>();
    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String groupId = getGroupId(session);
        groupSessions.computeIfAbsent(groupId, k -> new ConcurrentHashMap<>()).put(session.getId(), session);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        GroupMessageDTO groupMessage = objectMapper.readValue(message.getPayload(), GroupMessageDTO.class);
        String groupId = groupMessage.getGroupId();

        for (WebSocketSession s : groupSessions.getOrDefault(groupId, new ConcurrentHashMap<>()).values()) {
            if (s.isOpen()) {
                s.sendMessage(new TextMessage(objectMapper.writeValueAsString(createMessagePayload(groupMessage))));
            }
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String groupId = getGroupId(session);
        groupSessions.getOrDefault(groupId, new ConcurrentHashMap<>()).remove(session.getId());
    }

    private String getGroupId(WebSocketSession session) {
        String query = session.getUri().getQuery();
        if (query != null && query.contains("=")) {
            return query.split("=")[1];
        }
        return null;
    }

    public void sendMessageToMember(GroupMessageDTO groupMessageDTO) throws Exception {
        String groupId = groupMessageDTO.getGroupId();
        TextMessage message = new TextMessage(objectMapper.writeValueAsString(createMessagePayload(groupMessageDTO)));

        for (WebSocketSession s : groupSessions.getOrDefault(groupId, new ConcurrentHashMap<>()).values()) {
            if (s.isOpen()) {
                try {
                    s.sendMessage(message);
                } catch (Exception e) {
                    logger.error("Failed to send message to session: " + s.getId(), e);
                }
            }
        }
    }

    private MessagePayload createMessagePayload(GroupMessageDTO groupMessageDTO) {
        MessagePayload payload = new MessagePayload();
        payload.setType("message");
        payload.setSenderUid(groupMessageDTO.getUid());
        payload.setSenderName(groupMessageDTO.getUsername());
        payload.setGroupId(groupMessageDTO.getGroupId());
        payload.setTimestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")));
        payload.setContent(groupMessageDTO.getMessage());
        return payload;
    }

    @Getter
    @Setter
    private static class MessagePayload {
        private String type;
        private String senderUid;
        private String senderName;
        private String groupId;
        private String timestamp;
        private String content;
    }
}