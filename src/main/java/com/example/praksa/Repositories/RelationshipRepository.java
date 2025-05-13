package com.example.praksa.Repositories;

import com.example.praksa.Models.Relationship;

import com.example.praksa.Models.UserNode;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RelationshipRepository extends Neo4jRepository<Relationship,Long> {

    @Query("MATCH (sender:User {email: $senderEmail})-[r:RELATES_TO]->(receiver:User {email: $receiverEmail}) RETURN r")
    Optional<Relationship> findBySenderAndReceiver(
            @Param("senderEmail") String senderEmail,
            @Param("receiverEmail") String receiverEmail);

    @Query("MATCH (u:User {email: $email})-[r:RELATES_TO]->(receiver:User) WHERE r.status = $status RETURN receiver")
    List<UserNode> findTargetUsersWithStatus(
            @Param("email") String email,
            @Param("status") int status);

    @Query("MATCH (sender:User)-[r:RELATES_TO]->(u:User {email: $email}) WHERE r.status = $status RETURN sender")
    List<UserNode> findSourceUsersWithStatus(
            @Param("email") String email,
            @Param("status") int status);

    @Query("MATCH (sender:User {email: $senderemail})-[r:RELATES_TO]->(receiver:User {email: $receiveremail}) " +
            "SET r.status = $status RETURN r")
    Relationship updateRelationshipStatus(
            @Param("senderemail") String senderemail,
            @Param("receiveremail") String receiveremail,
            @Param("status") int status);

    @Query("MATCH (u1:User {email: $email1}), (u2:User {email: $email2}) " +
            "CREATE (u1)-[r1:RELATES_TO {status: 1, createdAt: datetime(), }]->(u2) " +
            "CREATE (u2)-[r2:RELATES_TO {status: 1, createdAt: datetime(), }]->(u1)")
    void createFriendship(
            @Param("email1") String email1,
            @Param("email2") String email2);

    @Query("MATCH (u1:User {email: $email1})-[r:RELATES_TO]->(u2:User {email: $email2}) DELETE r")
    void deleteRelationship(
            @Param("email1") String email1,
            @Param("email2") String email2);

    @Query("MATCH (sender:User {email: $senderemail}), (receiver:User {email: $receiveremail}) " +
            "CREATE (sender)-[r:RELATES_TO {status: 0, createdAt: datetime()}]->(receiver) RETURN r")
    Relationship createRelationship(
            @Param("senderemail") String senderemail,
            @Param("receiveremail") String receiveremail);

}


