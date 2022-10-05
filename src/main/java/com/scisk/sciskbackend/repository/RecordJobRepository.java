package com.scisk.sciskbackend.repository;

import com.scisk.sciskbackend.entity.RecordJob;
import com.scisk.sciskbackend.entity.RecordStep;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecordJobRepository extends MongoRepository<RecordJob, Long> {
    
}
