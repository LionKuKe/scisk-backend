package com.scisk.sciskbackend.repository;

import com.scisk.sciskbackend.datasourceentity.RecordDS;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface RecordRepository extends MongoRepository<RecordDS, Long> {

    List<RecordDS> findAllByCreatedOnBetween(Instant start, Instant end);

    List<RecordDS> findAllByCode(String code);
}
