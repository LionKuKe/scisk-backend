package com.scisk.sciskbackend.repository;

import com.scisk.sciskbackend.datasourceentity.UserDS;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<UserDS, Long> {

    @Query("{email:'?0'}")
    List<UserDS> existsByEmail(String email);

    @Query("{email:'?0'}")
    Optional<UserDS> findByEmail(String email);
}
