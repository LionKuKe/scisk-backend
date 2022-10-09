package com.scisk.sciskbackend.inputdatasource;

import com.scisk.sciskbackend.datasourceentity.RecordJobDS;
import com.scisk.sciskbackend.entity.RecordJob;
import com.scisk.sciskbackend.repository.RecordJobRepository;

import java.util.List;
import java.util.stream.Collectors;

public class RecordJobInputDSImpl implements RecordJobInputDS  {

    private final RecordJobRepository recordJobRepository;

    public RecordJobInputDSImpl(RecordJobRepository recordJobRepository) {
        this.recordJobRepository = recordJobRepository;
    }

    @Override
    public void saveAll(List<RecordJob> recordJobs) {
        recordJobRepository.saveAll(recordJobs.stream().map(RecordJobDS::map).collect(Collectors.toList()));
    }
}
