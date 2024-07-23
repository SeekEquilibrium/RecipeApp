package com.example.praksa.Config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.messaging.MessageSecurityMetadataSourceRegistry;
import org.springframework.security.config.annotation.web.socket.AbstractSecurityWebSocketMessageBrokerConfigurer;

@Configuration
public class ApplicationSocketSecurityConfiguration  extends AbstractSecurityWebSocketMessageBrokerConfigurer {

    @Override
    protected void configureInbound(MessageSecurityMetadataSourceRegistry messages) {
        messages
                .nullDestMatcher().authenticated()
                .simpDestMatchers("/app/**").hasAnyRole("ROLE_ADMIN", "ROLE_USER")
                .simpSubscribeDestMatchers("/topic/**","/queue/**", "/chat/**", "/user/**").hasAnyRole("ROLE_ADMIN", "ROLE_USER")
                .anyMessage().denyAll();
    }

    @Override
    protected boolean sameOriginDisabled() {
        return true;
    }
}
