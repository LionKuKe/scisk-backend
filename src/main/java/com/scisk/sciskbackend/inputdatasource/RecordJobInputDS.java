package com.scisk.sciskbackend.inputdatasource;

import com.scisk.sciskbackend.entity.RecordJob;

import java.util.List;
import java.util.Optional;

public interface RecordJobInputDS {

    void saveAll(List<RecordJob> recordJobs);

    List<RecordJob> findAllByJob(Long jobId);

    RecordJob save(RecordJob job);

    Optional<RecordJob> findById(Long idValue);
}
