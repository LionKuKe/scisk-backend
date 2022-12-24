package com.scisk.sciskbackend.inputdatasource;

import com.scisk.sciskbackend.entity.RecordJob;

import java.util.List;

public interface RecordJobInputDS {

    void saveAll(List<RecordJob> recordJobs);

    List<RecordJob> findAllByJob(Long jobId);
}
