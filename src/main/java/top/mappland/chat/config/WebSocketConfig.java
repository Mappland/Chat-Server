package top.mappland.chat.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import top.mappland.chat.handler.ChatWebSocketHandler;
import top.mappland.chat.mapper.UserMapper;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final UserMapper userMapper;

    public WebSocketConfig(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(chatWebSocketHandler(), "/chat").setAllowedOrigins("*");
    }

    @Bean
    public ChatWebSocketHandler chatWebSocketHandler() {
        ChatWebSocketHandler handler = new ChatWebSocketHandler();
        handler.setUserMapper(userMapper);
        return handler;
    }
}
