package com.scisk.sciskbackend.repository;

import com.scisk.sciskbackend.datasourceentity.NeededDocumentDS;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NeededDocumentRepository extends MongoRepository<NeededDocumentDS, Long> {
    
}
