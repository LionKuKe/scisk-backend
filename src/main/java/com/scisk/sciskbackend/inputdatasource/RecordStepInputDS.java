package com.scisk.sciskbackend.inputdatasource;

import com.scisk.sciskbackend.entity.RecordStep;

import java.util.List;
import java.util.Optional;

public interface RecordStepInputDS {

    List<RecordStep> findAllByRecordId(Long recordId);

    void saveAll(List<RecordStep> recordSteps);

    Optional<RecordStep> findById(Long idValue);

    Optional<RecordStep> findByRecordJobId(Long id);
}
