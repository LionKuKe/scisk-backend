package com.scisk.sciskbackend.inputdatasource;

import com.scisk.sciskbackend.entity.Job;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobInputDS extends MongoRepository<Job, Long> {
    List<Job> findAllByStepId(Long stepId);
}
