package com.example.praksa.Services;

import com.example.praksa.Converters.UserDTOConverter;
import com.example.praksa.DTOs.FriendResponseDTO;
import com.example.praksa.Models.Relationship;
import com.example.praksa.Models.UserApp;
import com.example.praksa.Models.UserNode;
import com.example.praksa.Repositories.neo4j.RelationshipRepository;
import com.example.praksa.Repositories.postgres.UserAppRepository;
import com.example.praksa.Repositories.neo4j.UserNodeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class RelationshipService {
    private final UserAppRepository userAppRepository;
    private final UserNodeRepository userNodeRepository;
    private final RelationshipRepository relationshipRepository;
    private final UserDTOConverter converter;

    public RelationshipService(UserAppRepository userAppRepository, UserNodeRepository userNodeRepository, RelationshipRepository relationshipRepository, UserDTOConverter converter) {
        this.userAppRepository = userAppRepository;
        this.userNodeRepository = userNodeRepository;
        this.relationshipRepository = relationshipRepository;
        this.converter = converter;
    }

    public boolean createFriendRequest(String email) throws Exception {
        UserApp userApp = (UserApp) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if(userNodeRepository.findByEmail(email).isEmpty()) {
           return false;
        }

        UserNode receiver = userNodeRepository.getUserNodeByEmail(email);

        // Check if a relationship already exists
        Optional<Relationship> existingRelationship =
                relationshipRepository.findBySenderAndReceiver(userApp.getEmail(), receiver.getEmail());

        if (existingRelationship.isPresent()) {
            return false; // Relationship already exists
        }

        // Also check reverse direction - if target has already sent a request to sender
        Optional<Relationship> reverseRelationship =
                relationshipRepository.findBySenderAndReceiver(userApp.getEmail(), receiver.getEmail());

        if (reverseRelationship.isPresent()) {
            // If they sent us a request,and we're sending one back, auto-accept it
            if (reverseRelationship.get().getStatus() == 0) {
                acceptFriendRequest(receiver.getEmail());
                return true;
            }
        }
        relationshipRepository.createRelationship(userApp.getEmail(),receiver.getEmail());
        return  true;
    }

    public boolean acceptFriendRequest(String receiverEmail) {
        UserApp userApp = (UserApp) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Optional<Relationship> requestOptional =
                relationshipRepository.findBySenderAndReceiver(receiverEmail, userApp.getEmail());

        if (requestOptional.isEmpty() || requestOptional.get().getStatus() != 0) {
            return false;
        }

        // Delete the pending request
        relationshipRepository.deleteRelationship(receiverEmail, userApp.getEmail());

        // Create a bidirectional friendship
        relationshipRepository.createFriendship(userApp.getEmail(), receiverEmail);

        return true;
    }

    public List<FriendResponseDTO> getPendingRequestsReceived() {
        UserApp userApp = (UserApp) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        List<UserNode> requests = relationshipRepository.findSourceUsersWithStatus(userApp.getEmail(), 0);
        return requests.stream().map(converter::userToFriendResponseDTO).toList();
    }

    public List<UserNode> getPendingRequestsSent(String username) {
        return relationshipRepository.findTargetUsersWithStatus(username, 0);
    }

    public boolean cancelFriendshipRequest(String receiverEmail) {
        UserApp userApp = (UserApp) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Optional<Relationship> requestOptional =
                relationshipRepository.findBySenderAndReceiver(receiverEmail, userApp.getEmail());

        if (requestOptional.isEmpty() || requestOptional.get().getStatus() != 0) {
            return false;
        }

        // Update the request status
        relationshipRepository.updateRelationshipStatus(
                userApp.getEmail(), receiverEmail, 2);

        return true;
    }

    public boolean blockUser( String blockedEmail) {
        // Delete any existing relationships in both directions
        UserApp userApp = (UserApp) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        relationshipRepository.deleteRelationship(userApp.getEmail(), blockedEmail);
        relationshipRepository.deleteRelationship(blockedEmail, userApp.getEmail());

        // Create a blocking relationship
        relationshipRepository.updateRelationshipStatus(userApp.getEmail(), blockedEmail, 2);

        return true;
    }

    public List<FriendResponseDTO> getFriends() {
        UserApp userApp = (UserApp) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<UserNode> friends =  relationshipRepository.findTargetUsersWithStatus(userApp.getEmail(), 1);
        return friends.stream().map(converter::userToFriendResponseDTO).toList();
    }



    public List<UserNode> getBlockedUsers() {
        UserApp userApp = (UserApp) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return relationshipRepository.findTargetUsersWithStatus(userApp.getEmail(), 2);
    }
}

