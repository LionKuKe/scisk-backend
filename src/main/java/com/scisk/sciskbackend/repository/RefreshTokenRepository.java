package com.scisk.sciskbackend.repository;

import com.scisk.sciskbackend.datasourceentity.RefreshToken;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends MongoRepository<RefreshToken, Long> {
    @Query("{token:'?0'}")
    Optional<RefreshToken> findByToken(String token);
}
