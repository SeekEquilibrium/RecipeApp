package com.example.praksa.Converters;

import com.example.praksa.DTOs.MessageDTO;
import com.example.praksa.Models.Message;
import com.example.praksa.Repositories.UserAppRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class MessageDTOConverter {

    private final UserAppRepository userAppRepository;

    public MessageDTOConverter(UserAppRepository userAppRepository) {
        this.userAppRepository = userAppRepository;
    }

    public MessageDTO messageToDTO (Message message){
        return MessageDTO.builder()
                .id(message.getId())
                .toUserId(message.getToUser().getId())
                .fromUserId(message.getFromUser().getId())
                .content(message.getContent())
                .time(message.getTime().toString())
                .build();
    }

}
