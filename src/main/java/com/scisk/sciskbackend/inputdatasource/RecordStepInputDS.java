package com.scisk.sciskbackend.inputdatasource;

import com.scisk.sciskbackend.entity.RecordStep;

import java.util.List;

public interface RecordStepInputDS {

    List<RecordStep> findAllByRecordId(Long recordId);

    void saveAll(List<RecordStep> recordSteps);
}
