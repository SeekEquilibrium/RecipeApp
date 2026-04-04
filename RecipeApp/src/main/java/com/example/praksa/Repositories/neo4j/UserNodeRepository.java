package com.example.praksa.Repositories.neo4j;


import com.example.praksa.Models.UserNode;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface UserNodeRepository extends Neo4jRepository<UserNode,String> {

    Optional<UserNode> findByEmail(String email);
    UserNode getUserNodeByEmail(String email);

    @Query("MATCH (u:User)-[r:RELATES_TO {status: 'FRIENDS'}]->(f:User) WHERE u.email = $email RETURN f")
    List<UserNode> findFriendsByEmail(@Param("username") String email);


}
