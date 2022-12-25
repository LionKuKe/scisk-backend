package com.scisk.sciskbackend.service;

import com.scisk.sciskbackend.dto.*;
import com.scisk.sciskbackend.entity.RecordJob;
import com.scisk.sciskbackend.entity.RecordStep;
import com.scisk.sciskbackend.entity.User;
import com.scisk.sciskbackend.exception.ObjectNotFoundException;
import com.scisk.sciskbackend.inputdatasource.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

@Transactional
@Service
public class RecordJobServImpl implements RecordJobService {

    private CounterService counterService;
    private final MongoTemplate mongoTemplate;
    private final JobInputDS jobInputDS;
    private final StepInputDS stepInputDS;
    private final RecordJobInputDS recordJobInputDS;
    private final UserInputDS userInputDS;
    private final RecordStepInputDS recordStepInputDS;

    public RecordJobServImpl(
            CounterService counterService,
            MongoTemplate mongoTemplate,
            JobInputDS jobInputDS,
            StepInputDS stepInputDS,
            RecordJobInputDS recordJobInputDS, UserInputDS userInputDS, RecordStepInputDS recordStepInputDS) {
        this.counterService = counterService;
        this.mongoTemplate = mongoTemplate;
        this.jobInputDS = jobInputDS;
        this.stepInputDS = stepInputDS;
        this.recordJobInputDS = recordJobInputDS;
        this.userInputDS = userInputDS;
        this.recordStepInputDS = recordStepInputDS;
    }


    @Override
    public void assignRecordJobToEmployee(Long id, Long userId, AssignRecordJobToEmployeeDto assignRecordJobToEmployeeDto) {
        RecordJob recordJob = recordJobInputDS.findById(id).orElseThrow(() -> new ObjectNotFoundException("id"));
        User user = userInputDS.findById(userId).orElseThrow(() -> new ObjectNotFoundException("userId"));
        recordJob.setEmployee(user);
        recordJob.setEstimatedStartDate(assignRecordJobToEmployeeDto.getEstimatedStartDate());
        recordJob.setEstimatedEndDate(assignRecordJobToEmployeeDto.getEstimatedEndDate());
        recordJobInputDS.save(recordJob);
    }

    @Override
    public void closeRecordJobByAssistant(Long id, CloseRecordJobByAssistantDto closeRecordJobByEmployeeDto) {
        RecordJob recordJob = recordJobInputDS.findById(id).orElseThrow(() -> new ObjectNotFoundException("id"));
        recordJob.setStartDate(closeRecordJobByEmployeeDto.getStartDate());
        recordJob.setEndDate(closeRecordJobByEmployeeDto.getEndDate());
        recordJob.setObservation(closeRecordJobByEmployeeDto.getObservation());
        recordJobInputDS.save(recordJob);
    }

    @Override
    public void closeRecordJobByChief(Long id, CloseRecordJobByChiefDto closeRecordJobByChiefDto) {
        RecordJob recordJob = recordJobInputDS.findById(id).orElseThrow(() -> new ObjectNotFoundException("id"));
        recordJob.setChiefEndDate(closeRecordJobByChiefDto.getChiefEndDate());
        recordJob.setChiefObservation(closeRecordJobByChiefDto.getChiefObservation());
        recordJobInputDS.save(recordJob);
        RecordStep recordStep = recordStepInputDS.findById(recordJob.getRecordStep().getId()).get();
        recordStep.setEndDate(closeRecordJobByChiefDto.getChiefEndDate());
        recordStepInputDS.saveAll(Arrays.asList(recordStep));
    }
}
