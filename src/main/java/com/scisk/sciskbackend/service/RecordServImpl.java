package com.scisk.sciskbackend.service;

import com.scisk.sciskbackend.dto.*;
import com.scisk.sciskbackend.entity.NeededDocument;
import com.scisk.sciskbackend.entity.Step;
import com.scisk.sciskbackend.exception.ObjectNotFoundException;
import com.scisk.sciskbackend.repository.NeededDocumentRepository;
import com.scisk.sciskbackend.repository.ServiceRepository;
import com.scisk.sciskbackend.repository.StepRepository;
import com.scisk.sciskbackend.repository.UserRepository;
import com.scisk.sciskbackend.util.GlobalParams;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Transactional
@Service
public class RecordServImpl implements RecordService {

    private UserRepository userRepository;
    private CounterService counterService;
    private StepRepository stepRepository;
    private NeededDocumentRepository neededDocumentRepository;
    private final MongoTemplate mongoTemplate;
    private final ServiceRepository serviceRepository;

    public RecordServImpl(
            UserRepository userRepository,
            CounterService counterService,
            StepRepository stepRepository,
            NeededDocumentRepository neededDocumentRepository,
            ServiceRepository serviceRepository,
            MongoTemplate mongoTemplate
    ) {
        this.userRepository = userRepository;
        this.counterService = counterService;
        this.stepRepository = stepRepository;
        this.neededDocumentRepository = neededDocumentRepository;
        this.mongoTemplate = mongoTemplate;
        this.serviceRepository = serviceRepository;
    }


    @Override
    public RecordReturnDto create(RecordCreateDto recordCreateDto) {
        return null;
    }

    @Override
    public RecordReturnDto update(Long idValue, RecordCreateDto recordCreateDto) {
        return null;
    }

    @Override
    public Page<RecordReturnDto> findAllRecordByFilters(Integer page, Integer size, String name, String description) {
        return null;
    }

    @Override
    public Page<RecordReturnDto> findAllForCustomers(Integer page, Integer size, String name, String description) {
        return null;
    }

    @Override
    public RecordReturnDto findById(Long idValue) {
        return null;
    }

    @Override
    public RecordReturnDto suspend(Long idValue) {
        return null;
    }
}