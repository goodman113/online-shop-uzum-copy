package project.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(vendorWebSocketHandler(), "/ws/vendor-updates")
                .setAllowedOrigins("*"); // or restrict to your domain
    }

    @Bean
    public WebSocketHandler vendorWebSocketHandler() {
        return new VendorUpdateWebSocketHandler();
    }
}