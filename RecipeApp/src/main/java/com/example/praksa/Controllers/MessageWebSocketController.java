package com.example.praksa.Controllers;

import com.example.praksa.Converters.MessageDTOConverter;
import com.example.praksa.DTOs.MessageDTO;
import com.example.praksa.DTOs.MessageRequestDTO;
import com.example.praksa.Models.Message;
import com.example.praksa.Services.MessageService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
public class MessageWebSocketController {

    private final SimpMessagingTemplate template;
    private final MessageService messageService;
    private final MessageDTOConverter converter;
    private final ObjectMapper objectMapper;

    public MessageWebSocketController(SimpMessagingTemplate template, MessageService messageService, MessageDTOConverter converter, ObjectMapper objectMapper) {
        this.template = template;
        this.messageService = messageService;
        this.converter = converter;
        this.objectMapper = objectMapper;
    }

    @MessageMapping("/message")
    public void createPrivateChatMessages(@Payload MessageRequestDTO messageCreateBindingModel, Principal principal) throws Exception {
        Message message = this.messageService.createMessage(messageCreateBindingModel, principal.getName());
        MessageDTO messageDTO = converter.messageToDTO(message);

        if (messageDTO != null) {
            String response = this.objectMapper.writeValueAsString(messageDTO);
            template.convertAndSend("/user/" + message.getToUser().getUsername() + "/queue/position-update", response);
            template.convertAndSend("/user/" + message.getFromUser().getUsername() + "/queue/position-update", response);
            return;
        }
        throw new Exception("Message failed to send");
    }

}