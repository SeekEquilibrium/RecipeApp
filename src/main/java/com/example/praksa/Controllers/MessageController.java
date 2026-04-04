package com.example.praksa.Controllers;

import com.example.praksa.Converters.MessageDTOConverter;
import com.example.praksa.DTOs.MessageDTO;
import com.example.praksa.Models.Message;
import com.example.praksa.Services.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController()
@RequestMapping(value = "/message")
public class MessageController {

    private final MessageService messageService;
    private final MessageDTOConverter converter;

    public MessageController(MessageService messageService, MessageDTOConverter converter) {
        this.messageService = messageService;
        this.converter = converter;
    }

    @GetMapping(value = "/all/{id}")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get all messages between the logged-in user and the given user", method = "GET")
    public List<MessageDTO> getAllMessages(@PathVariable(value = "id") Long chatUserId) throws Exception {
        List<Message> messages = this.messageService.getAllMessages(chatUserId);
        return messages.stream().map(converter::messageToDTO).collect(Collectors.toList());
    }

}