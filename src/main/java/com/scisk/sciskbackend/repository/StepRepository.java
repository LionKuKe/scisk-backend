package com.scisk.sciskbackend.repository;

import com.scisk.sciskbackend.entity.Service;
import com.scisk.sciskbackend.entity.Step;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StepRepository extends MongoRepository<Step, Long> {
    
}
