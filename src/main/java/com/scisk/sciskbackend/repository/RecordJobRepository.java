package com.scisk.sciskbackend.repository;

import com.scisk.sciskbackend.datasourceentity.RecordJobDS;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecordJobRepository extends MongoRepository<RecordJobDS, Long> {
    
}
