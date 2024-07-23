package com.example.praksa.Services;

import com.example.praksa.Converters.UserDTOConverter;
import com.example.praksa.DTOs.FriendResponseDTO;
import com.example.praksa.DTOs.UserDTO;
import com.example.praksa.Models.Relationship;
import com.example.praksa.Models.UserApp;
import com.example.praksa.Repositories.RelationshipRepository;
import com.example.praksa.Repositories.UserAppRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class RelationshipService {
    private final UserAppRepository userAppRepository;
    private final RelationshipRepository relationshipRepository;
    private final UserDTOConverter converter;

    public RelationshipService(UserAppRepository userAppRepository, RelationshipRepository relationshipRepository, UserDTOConverter converter) {
        this.userAppRepository = userAppRepository;
        this.relationshipRepository = relationshipRepository;
        this.converter = converter;
    }

    public boolean createFriendRequest (Long userTwoId) throws Exception{
        UserApp userApp = (UserApp) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        UserApp friend = userAppRepository.findById(userTwoId).orElseThrow(() -> {
            log.error("User not found");
            return new Exception("User not found");
        });

        Relationship relationshipFromDb = relationshipRepository.findRelationshipByUserOneIdAndUserTwoId(userApp.getId(),friend.getId());
        if (relationshipFromDb == null) {
            Relationship relationship = new Relationship();
            relationship.setActionUser(userApp);
            relationship.setUserOne(userApp);
            relationship.setUserTwo(friend);
            relationship.setStatus(0);
            relationship.setTime(LocalDateTime.now());

            this.relationshipRepository.save(relationship);
            return true;
        } else {
            relationshipFromDb.setActionUser(userApp);
            relationshipFromDb.setStatus(0);
            relationshipFromDb.setTime(LocalDateTime.now());
            this.relationshipRepository.save(relationshipFromDb);
            return true;
        }

    }

    public List<FriendResponseDTO> getAllFriends(){

        UserApp userApp = (UserApp) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<Relationship> relationships = this.relationshipRepository.findRelationshipByUserIdAndStatus(userApp.getId(),1);

        List<FriendResponseDTO> response = relationships.stream().map(relationship -> {
            if(!(relationship.getUserOne().getId() == userApp.getId())){
                return converter.userToFriendResponseDTO(relationship.getUserOne());
            }
            return converter.userToFriendResponseDTO(relationship.getUserTwo());
        }).collect(Collectors.toList());

        return response;
    }

    public List<FriendResponseDTO> getAllRequests(){

        UserApp userApp = (UserApp) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<Relationship> relationships = this.relationshipRepository.findRelationshipByUserIdAndStatus(userApp.getId(),0);

        List<FriendResponseDTO> response = relationships.stream().map(relationship -> {
            if(!(relationship.getUserOne().getId() == userApp.getId())){
                return converter.userToFriendResponseDTO(relationship.getUserOne());
            }
            return converter.userToFriendResponseDTO(relationship.getUserTwo());
        }).collect(Collectors.toList());

        return response;
    }

    public boolean removeFriend (Long friendId) throws Exception {
        return this.changeStatusAndSave(friendId,1,2);
    }

    public boolean acceptFriend(Long friendToAcceptId) throws Exception {
        return this.changeStatusAndSave( friendToAcceptId, 0, 1);
    }

    public boolean cancelFriendshipRequest(Long friendToRejectId) throws Exception {
        return this.changeStatusAndSave(friendToRejectId, 0, 2);
    }

    private boolean changeStatusAndSave(Long friendId, int fromStatus, int toStatus) throws Exception {
        UserApp loggedInUser = (UserApp) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        UserApp friend = userAppRepository.findById(friendId).orElseThrow(() -> {
            log.error("User not found");
            return new Exception("User not found");
        });

        Relationship relationship = this.relationshipRepository
                .findRelationshipWithFriendWithStatus(
                        loggedInUser.getId(), friendId, fromStatus);

        if (relationship == null) {
            throw new Exception("Relationship doesnt exist");
        }

        relationship.setActionUser(loggedInUser);
        relationship.setStatus(toStatus);
        relationship.setTime(LocalDateTime.now());
        this.relationshipRepository.save(relationship);
        return true;
    }
}
