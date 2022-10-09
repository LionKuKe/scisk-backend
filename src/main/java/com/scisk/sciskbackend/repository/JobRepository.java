package com.scisk.sciskbackend.repository;

import com.scisk.sciskbackend.datasourceentity.JobDS;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobRepository extends MongoRepository<JobDS, Long> {
    List<JobDS> findAllByStepId(Long stepId);
}
