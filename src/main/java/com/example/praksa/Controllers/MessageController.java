package com.example.praksa.Controllers;

import com.example.praksa.Converters.MessageDTOConverter;
import com.example.praksa.DTOs.MessageDTO;
import com.example.praksa.DTOs.MessageRequestDTO;
import com.example.praksa.Models.Message;
import com.example.praksa.Models.UserApp;
import com.example.praksa.Services.MessageService;
import io.swagger.annotations.ApiOperation;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController()
@RequestMapping(value = "/message")
public class MessageController {

    private final SimpMessagingTemplate template;
    private final MessageService messageService;
    private final MessageDTOConverter converter;
    private final ObjectMapper objectMapper;

    public MessageController(SimpMessagingTemplate template, MessageService messageService, MessageDTOConverter converter, ObjectMapper objectMapper) {
        this.template = template;
        this.messageService = messageService;
        this.converter = converter;
        this.objectMapper = objectMapper;
    }

    @GetMapping(value = "/all/{id}")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')" )
    @ApiOperation(value = "Get all of users friend requests",httpMethod = "GET")
    public List<MessageDTO> getAllMessages(@PathVariable(value = "id") Long chatUserId) throws Exception {
        List<Message> messages = this.messageService.getAllMessages(chatUserId);
        return messages.stream().map(converter::messageToDTO).collect(Collectors.toList());
    }
    @GetMapping(value = "/message")
    @MessageMapping("/message")
    public void createPrivateChatMessages(@RequestBody MessageRequestDTO messageCreateBindingModel, Principal principal, SimpMessageHeaderAccessor headerAccessor) throws Exception {
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
