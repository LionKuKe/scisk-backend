package com.scisk.sciskbackend.inputdatasource;

import com.scisk.sciskbackend.entity.Step;

import java.util.Optional;

public interface StepInputDS {

    Step save(Step step);

    Optional<Step> findById(Long stepIdValue);
}
