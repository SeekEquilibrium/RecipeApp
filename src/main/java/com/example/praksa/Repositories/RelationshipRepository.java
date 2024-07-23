package com.example.praksa.Repositories;

import com.example.praksa.Models.Relationship;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RelationshipRepository extends JpaRepository<Relationship,Long> {

    @Query(value = "" +
            "SELECT r FROM Relationship AS r " +
            "WHERE ((r.userOne.id = :id1 AND r.userTwo.id = :id2) " +
            "OR ( r.userTwo.id = :id1 AND r.userOne.id = :id2)) ")
    Relationship findRelationshipByUserOneIdAndUserTwoId(@Param(value = "id1") Long userOneId,
                                                         @Param(value = "id2") Long userTwoId);

    @Query(value = "" +
            "SELECT r FROM Relationship AS r " +
            "WHERE (r.userOne.id = :id OR r.userTwo.id = :id ) " +
            "AND r.status = :status")
    List<Relationship> findRelationshipByUserIdAndStatus(@Param(value = "id") Long userId,
                                                         @Param(value = "status") int status);

    @Query(value = "" +
            "SELECT r FROM Relationship AS r " +
            "WHERE ((r.userOne.id = :id1 AND r.userTwo.id = :id2) " +
            "OR ( r.userTwo.id = :id1 AND r.userOne.id = :id2)) " +
            "AND r.status = :status")
    Relationship findRelationshipWithFriendWithStatus(@Param(value = "id1") Long userOneId,
                                                      @Param(value = "id2") Long userTwoId,
                                                      @Param(value = "status") int status);

}


