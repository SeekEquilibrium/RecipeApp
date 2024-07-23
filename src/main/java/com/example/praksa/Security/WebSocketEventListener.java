package com.example.praksa.Security;

import com.example.praksa.Models.UserApp;
import com.example.praksa.Services.UserAppService;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import static com.example.praksa.Security.WebSocketEventName.CONNECT;
import static com.example.praksa.Security.WebSocketEventName.DISCONNECT;

@Component
public class WebSocketEventListener {
    private final UserAppService userAppService;
    private final SimpMessagingTemplate template;

    public WebSocketEventListener(UserAppService userAppService, SimpMessagingTemplate template) {
        this.userAppService = userAppService;
        this.template = template;
    }
    @EventListener
    private void handleSessionConnected(SessionConnectEvent event) throws Exception {
        SimpMessageHeaderAccessor headers = SimpMessageHeaderAccessor.wrap(event.getMessage());
        String username = headers.getUser().getName();

        UserApp userApp = userAppService.findByEmail(username);
        WebSocketMessage message = new WebSocketMessage(CONNECT, userApp.getId(), username, true);

        template.convertAndSend(CONNECT.getDestination(), message);
    }

    @EventListener
    private void handleSessionDisconnect(SessionDisconnectEvent event) throws Exception {
        SimpMessageHeaderAccessor headers = SimpMessageHeaderAccessor.wrap(event.getMessage());
        String username = headers.getUser().getName();

        UserApp userApp = userAppService.findByEmail(username);
        WebSocketMessage message = new WebSocketMessage(DISCONNECT, userApp.getId(), username, false);

        template.convertAndSend(DISCONNECT.getDestination(), message);
    }
}
