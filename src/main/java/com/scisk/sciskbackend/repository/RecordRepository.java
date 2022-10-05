package com.scisk.sciskbackend.repository;

import com.scisk.sciskbackend.entity.Record;
import com.scisk.sciskbackend.entity.Service;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface RecordRepository extends MongoRepository<Record, Long> {

    List<Record> findAllByCreatedOnBetween(Instant start, Instant end);

    List<Record> findAllByCode(String code);
}
