package com.scisk.sciskbackend.inputdatasource;

import com.scisk.sciskbackend.datasourceentity.JobDS;
import com.scisk.sciskbackend.entity.Job;
import com.scisk.sciskbackend.repository.JobRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Transactional
@Service
public class JobInputDSImpl implements JobInputDS {

    private final JobRepository jobRepository;

    public JobInputDSImpl(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    @Override
    public List<Job> findAllByStepId(Long stepId) {
        return jobRepository.findAllByStepId(stepId).stream().map(JobDS::map).collect(Collectors.toList());
    }

    @Override
    public Job save(Job job) {
        jobRepository.save(JobDS.map(job));
        return job;
    }

    @Override
    public Optional<Job> findById(Long idValue) {
        return jobRepository.findById(idValue).map(JobDS::map);
    }

    @Override
    public void delete(Job job) {
        jobRepository.delete(JobDS.map(job));
    }

}
