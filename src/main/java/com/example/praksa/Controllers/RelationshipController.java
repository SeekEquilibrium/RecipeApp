package com.example.praksa.Controllers;

import com.example.praksa.DTOs.FriendResponseDTO;
import com.example.praksa.DTOs.UserDTO;
import com.example.praksa.Services.RelationshipService;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/relationship")
public class RelationshipController {

    private final RelationshipService relationshipService;

    public RelationshipController(RelationshipService relationshipService) {
        this.relationshipService = relationshipService;
    }

    @PostMapping(value = "/addFriend")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    @ApiOperation(value = "Send a friend request to user",httpMethod = "POST")
    public ResponseEntity<?> addFriend(@RequestParam String email) throws Exception {
        boolean result = relationshipService.createFriendRequest(email);
        if(result){
            return  ResponseEntity.ok("Friend request successfully sent ");
        }
        throw new Exception("Friend request failed to be sent");
    }

    @GetMapping(value = "/getFriends")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    @ApiOperation(value = "Get all of users friends",httpMethod = "GET")
    public List<FriendResponseDTO> getAllFriends() {
        return this.relationshipService.getFriends();
    }

    @GetMapping(value = "/getRequests")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    @ApiOperation(value = "Get all of users friend requests",httpMethod = "GET")
    public List<FriendResponseDTO> getAllRequests() {
        return this.relationshipService.getPendingRequestsReceived();
    }

    @PostMapping(value = "/acceptFriend")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    @ApiOperation(value = "Accept friend request ",httpMethod = "POST")
    public ResponseEntity<?> acceptFriendRequest(@RequestParam String  email) throws Exception{
        boolean result = relationshipService.acceptFriendRequest(email);
        if(result){
            return  ResponseEntity.ok("Friend request accepted successfully");
        }
        throw new Exception("Friend accepted  failed");
    }

    @PostMapping(value = "/cancelFriendRequest")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    @ApiOperation(value = "Cancel friend request",httpMethod = "POST")
    public ResponseEntity<?> cancelFriendRequest(@RequestParam String email) throws Exception{
        boolean result = relationshipService.cancelFriendshipRequest(email);
        if(result){
            return  ResponseEntity.ok("Friend request cancelled successfully");
        }
        throw new Exception("Friend request cancel failed");
    }

    @PostMapping(value = "/removeFriend")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    @ApiOperation(value = "Remove friend from  users friends",httpMethod = "POST")
    public ResponseEntity<?> removeFriend(@RequestParam String email) throws Exception{
        boolean result = relationshipService.blockUser(email);
        if(result){
            return  ResponseEntity.ok("Friend removed successfully");
        }
        throw new Exception("Friend removed successfully");
    }
}
