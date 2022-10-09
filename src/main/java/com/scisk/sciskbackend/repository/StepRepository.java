package com.scisk.sciskbackend.repository;

import com.scisk.sciskbackend.datasourceentity.StepDS;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StepRepository extends MongoRepository<StepDS, Long> {
}
