package com.example.praksa.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.config.annotation.web.socket.EnableWebSocketSecurity;
import org.springframework.security.messaging.access.intercept.MessageMatcherDelegatingAuthorizationManager;

@Configuration
@EnableWebSocketSecurity
public class ApplicationSocketSecurityConfiguration {

    @Bean
    AuthorizationManager<Message<?>> messageAuthorizationManager(
            MessageMatcherDelegatingAuthorizationManager.Builder messages) {
        messages
                .nullDestMatcher().authenticated()
                .simpDestMatchers("/app/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_USER")
                .simpSubscribeDestMatchers("/topic/**", "/queue/**", "/chat/**", "/user/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_USER")
                .anyMessage().denyAll();

        return messages.build();
    }

    // Disable WebSocket CSRF — we use JWT, not session-based CSRF tokens
    @Bean(name = "csrfChannelInterceptor")
    public ChannelInterceptor csrfChannelInterceptor() {
        return new ChannelInterceptor() {};
    }
}