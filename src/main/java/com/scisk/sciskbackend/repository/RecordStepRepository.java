package com.scisk.sciskbackend.repository;

import com.scisk.sciskbackend.entity.RecordStep;
import com.scisk.sciskbackend.entity.Step;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecordStepRepository extends MongoRepository<RecordStep, Long> {

    @Query("{recordId:'?0'}")
    List<RecordStep> findAllByRecordId(Long recordId);
    
}
