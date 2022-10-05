package com.scisk.sciskbackend.repository;

import com.scisk.sciskbackend.entity.Job;
import com.scisk.sciskbackend.entity.RecordJob;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobRepository extends MongoRepository<Job, Long> {
    List<Job> findAllByStepId(Long stepId);
}
