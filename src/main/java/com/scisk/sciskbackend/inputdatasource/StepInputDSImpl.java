package com.scisk.sciskbackend.inputdatasource;

import com.scisk.sciskbackend.datasourceentity.StepDS;
import com.scisk.sciskbackend.entity.Step;
import com.scisk.sciskbackend.repository.StepRepository;

import java.util.Optional;

public class StepInputDSImpl implements StepInputDS {

    private final StepRepository stepRepository;

    public StepInputDSImpl(StepRepository stepRepository) {
        this.stepRepository = stepRepository;
    }

    @Override
    public Step save(Step step) {
        stepRepository.save(StepDS.map(step));
        return step;
    }

    @Override
    public Optional<Step> findById(Long stepIdValue) {
        return stepRepository.findById(stepIdValue).map(StepDS::map);
    }
}
