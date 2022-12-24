package com.scisk.sciskbackend.inputdatasource;

import com.scisk.sciskbackend.entity.Job;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JobInputDS {
    List<Job> findAllByStepId(Long stepId);

    Job save(Job job);

    Optional<Job> findById(Long idValue);

    void delete(Job job);
}
