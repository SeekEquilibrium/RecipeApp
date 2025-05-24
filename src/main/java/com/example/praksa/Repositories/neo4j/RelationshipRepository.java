package com.example.praksa.Repositories.neo4j;

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

    @Query("MATCH (sender:UserNode {email: $senderEmail})-[r:RELATES_TO]->(receiver:UserNode {email: $receiverEmail}) RETURN r")
    Optional<Relationship> findBySenderAndReceiver(
            @Param("senderEmail") String senderEmail,
            @Param("receiverEmail") String receiverEmail);

    @Query("MATCH (u:UserNode {email: $email})-[r:RELATES_TO]->(receiver:UserNode) WHERE r.status = $status RETURN " +
            "receiver.email as email , receiver.name as name , receiver.surname as surname , receiver.password as password , receiver.phoneNumber as phoneNumber")
    List<UserNode> findTargetUsersWithStatus(
            @Param("email") String email,
            @Param("status") int status);

    @Query("OPTIONAL MATCH (sender:UserNode)-[r:RELATES_TO]->(u:UserNode {email: $email}) WHERE r.status = $status RETURN" +
            " sender.email as email , sender.name as name , sender.surname as surname , sender.password as password , sender.phoneNumber as phoneNumber")
    List<UserNode> findSourceUsersWithStatus(
            @Param("email") String email,
            @Param("status") int status);

    @Query("MATCH (sender:UserNode {email: $senderemail})-[r:RELATES_TO]->(receiver:UserNode {email: $receiveremail}) " +
            "SET r.status = $status RETURN r")
    Relationship updateRelationshipStatus(
            @Param("senderemail") String senderemail,
            @Param("receiveremail") String receiveremail,
            @Param("status") int status);

    @Query("MATCH (u1:UserNode {email: $email1}), (u2:UserNode {email: $email2}) " +
            "CREATE (u1)-[r1:RELATES_TO {status: 1, createdAt: datetime()}]->(u2) " +
            "CREATE (u2)-[r2:RELATES_TO {status: 1, createdAt: datetime()}]->(u1)")
    void createFriendship(
            @Param("email1") String email1,
            @Param("email2") String email2);

    @Query("MATCH (u1:UserNode {email: $email1})-[r:RELATES_TO]->(u2:UserNode {email: $email2}) DELETE r")
    void deleteRelationship(
            @Param("email1") String email1,
            @Param("email2") String email2);

    @Query("MATCH (sender:UserNode {email: $senderemail}), (receiver:UserNode {email: $receiveremail}) " +
            "CREATE (sender)-[r:RELATES_TO {status: 0, createdAt: datetime()}]->(receiver) RETURN r")
    Relationship createRelationship(
            @Param("senderemail") String senderemail,
            @Param("receiveremail") String receiveremail);

}


