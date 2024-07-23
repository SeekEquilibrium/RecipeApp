package com.example.praksa.Config;

import com.example.praksa.Models.JWTAuthenticationToken;
import com.example.praksa.Models.UserApp;
import com.example.praksa.Security.TokenHandler;
import com.example.praksa.Services.UserAppService;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import io.jsonwebtoken.Jwts;
import java.util.Optional;

@Configuration
@EnableWebSocketMessageBroker
public class ApplicationWebSocketConfiguration implements WebSocketMessageBrokerConfigurer {


    private final TokenHandler tokenHandler;
    private final UserAppService userAppService;

    public ApplicationWebSocketConfiguration(TokenHandler tokenHandler, UserAppService userAppService) {
        this.tokenHandler = tokenHandler;
        this.userAppService = userAppService;
    }

    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Register the "/socket" endpoint, enabling the SockJS protocol.
        // SockJS is used (both client and server side) to allow alternative
        // messaging options if WebSocket is not available.
        registry.addEndpoint("/socket")
                .setAllowedOrigins("*")
                .withSockJS();
    }

    public void configureMessageBroker(MessageBrokerRegistry registry) {
//    // Enable a simple memory-based message broker to send messages to the
//    // client on destinations prefixed with "/app".
//    // Simple message broker handles subscription requests from clients, stores
//    // them in memory, and broadcasts messages to connected clients with
//    // matching destinations.
        registry.setApplicationDestinationPrefixes("/app")
                .setUserDestinationPrefix("/user")
                .enableSimpleBroker("/chat", "/topic", "/queue");
    }

    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {

            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor =
                        MessageHeaderAccessor.getAccessor( message, StompHeaderAccessor.class);
                if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                    Optional.ofNullable(accessor.getNativeHeader("Authorization")).ifPresent(ah -> {
                        String bearerToken = ah.get(0).replace("Bearer ", "");
                        JWTAuthenticationToken token = getJWTAuthenticationToken(bearerToken);
                        accessor.setUser(token);
                    });
                }
                return message;
            }
        });
    }

    private JWTAuthenticationToken getJWTAuthenticationToken(String token) {
        if (token != null) {
            String username = Jwts.parser()
                    .setSigningKey("Secret".getBytes())
                    .parseClaimsJws(token.replace("Bearer ", ""))
                    .getBody()
                    .getSubject();

            if (username != null) {
                UserApp userData = this.userAppService.findByEmail(username);


                JWTAuthenticationToken jwtAuthenticationToken =
                        new JWTAuthenticationToken(userData.getAuthorities(), token, userData);

                jwtAuthenticationToken.setAuthenticated(true);

                return jwtAuthenticationToken;
            }
        }

        return null;
    }
}
