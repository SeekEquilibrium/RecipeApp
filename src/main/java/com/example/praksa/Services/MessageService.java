package com.example.praksa.Services;

import com.example.praksa.DTOs.MessageRequestDTO;
import com.example.praksa.Models.Message;
import com.example.praksa.Models.Relationship;
import com.example.praksa.Models.UserApp;
import com.example.praksa.Repositories.MessageRepository;
import com.example.praksa.Repositories.RelationshipRepository;
import com.example.praksa.Repositories.UserAppRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class MessageService {

    private final UserAppRepository userAppRepository;
    private final MessageRepository messageRepository;
    private final RelationshipRepository relationshipRepository;

    public MessageService(UserAppRepository userAppRepository, MessageRepository messageRepository, RelationshipRepository relationshipRepository) {
        this.userAppRepository = userAppRepository;
        this.messageRepository = messageRepository;
        this.relationshipRepository = relationshipRepository;
    }

    private void updateMessageStatus(Long loggedInUserId, Long chatUserId) {
        this.messageRepository.updateStatusFromReadMessages(loggedInUserId, chatUserId);
    }
    public List<Message> getAllMessages(Long chatUserId) throws Exception {
        UserApp userApp = (UserApp) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        UserApp friend = userAppRepository.findById(chatUserId).orElseThrow(() -> {
            log.error("User not found");
            return new Exception("User not found");
        });

        List<Message> messages = this.messageRepository
                .findAllMessagesBetweenTwoUsers(userApp.getId(), friend.getId());

        this.updateMessageStatus(userApp.getId(), chatUserId);

        return messages;
    }

    public Message createMessage(MessageRequestDTO messageCreateBindingModel, String loggedInUsername) throws Exception {


        UserApp fromUser = userAppRepository.findByEmail(loggedInUsername);
        if(fromUser==null) {
            log.error("User not found");
            throw  new Exception("User not found");
        }

        UserApp toUser = userAppRepository.findById(messageCreateBindingModel.getToUserId()).orElseThrow(() -> {
            log.error("User not found");
            return new Exception("User not found");
        });

        Relationship relationship = relationshipRepository.findRelationshipWithFriendWithStatus(fromUser.getId(), toUser.getId(), 1);

        if (relationship == null) {
            throw  new Exception("Relationship not found");
        }

        Message message = new Message();
        message.setContent(messageCreateBindingModel.getContent());
        message.setFromUser(fromUser);
        message.setToUser(toUser);
        message.setRelationship(relationship);
        message.setTime(LocalDateTime.now());

        messageRepository.save(message);

        if(message != null){
            return  message;
        }

        throw new Exception("ERROR SENDING MESSAGE");
    }
}
