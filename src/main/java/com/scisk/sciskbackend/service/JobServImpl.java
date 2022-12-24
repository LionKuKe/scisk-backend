package com.scisk.sciskbackend.service;

import com.scisk.sciskbackend.dto.*;
import com.scisk.sciskbackend.entity.Job;
import com.scisk.sciskbackend.entity.Step;
import com.scisk.sciskbackend.exception.ObjectExistsException;
import com.scisk.sciskbackend.exception.ObjectNotFoundException;
import com.scisk.sciskbackend.inputdatasource.*;
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

import java.util.Objects;
import java.util.stream.Collectors;

@Transactional
@Service
public class JobServImpl implements JobService {

    private CounterService counterService;
    private final MongoTemplate mongoTemplate;
    private final JobInputDS jobInputDS;
    private final StepInputDS stepInputDS;
    private final RecordJobInputDS recordJobInputDS;

    public JobServImpl(
            CounterService counterService,
            MongoTemplate mongoTemplate,
            JobInputDS jobInputDS,
            StepInputDS stepInputDS,
            RecordJobInputDS recordJobInputDS) {
        this.counterService = counterService;
        this.mongoTemplate = mongoTemplate;
        this.jobInputDS = jobInputDS;
        this.stepInputDS = stepInputDS;
        this.recordJobInputDS = recordJobInputDS;
    }

    @Override
    public JobReturnDto create(JobCreateDto jobCreateDto) {
        Step step = stepInputDS.findById(jobCreateDto.getStepId()).orElseThrow(() -> new ObjectNotFoundException("stepId"));
        Job job = Job.builder()
                .id(counterService.getNextSequence(GlobalParams.JOB_COLLECTION_NAME))
                .name(jobCreateDto.getName())
                .description(jobCreateDto.getDescription())
                .order(jobCreateDto.getOrder())
                .step(step)
                .build();
        jobInputDS.save(job);
        return JobReturnDto.map(job);
    }

    @Override
    public JobReturnDto update(Long idValue, JobUpdateDto jobUpdateDto) {
        Job job = jobInputDS.findById(idValue).orElseThrow(() -> new ObjectNotFoundException("id"));
        Step step = stepInputDS.findById(jobUpdateDto.getStepId()).orElseThrow(() -> new ObjectNotFoundException("stepId"));
        job.setName(jobUpdateDto.getName());
        job.setDescription(jobUpdateDto.getDescription());
        job.setOrder(jobUpdateDto.getOrder());
        job.setStep(step);
        jobInputDS.save(job);
        return JobReturnDto.map(job);
    }

    @Override
    public Page<JobReturnDto> findAllByFilters(Integer page, Integer size, String name, String description, Long stepId) {
        int pageNumber = Objects.isNull(page) ? 0 : page - 1;
        int pageSize = Objects.isNull(size) ? GlobalParams.GLOBAL_DEFAULT_PAGE_SIZE : size;
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.Direction.DESC, "_id");

        AggregationOperation lookupAggStep = Aggregation.lookup("step", "stepId", "_id", "step");
        AggregationOperation unwindAggStep = Aggregation.unwind("step", true);
        AggregationOperation lookupAggService = Aggregation.lookup("service", "step.serviceId", "_id", "step.service");
        AggregationOperation unwindAggService = Aggregation.unwind("step.service", true);

        Criteria criteria = new Criteria();
        if (!Objects.isNull(stepId)) {
            criteria.and("stepId").equals(stepId);
        }
        if (StringUtils.isNotBlank(name)) {
            criteria.and("name").regex(name, "i");
        }
        if (StringUtils.isNotBlank(description)) {
            criteria.and("description").regex(description, "i");
        }
        AggregationOperation matchAgg = Aggregation.match(criteria);

        AggregationOperation sortAgg = Aggregation.sort(Sort.Direction.DESC, "_id");
        AggregationOperation skipAgg = Aggregation.skip(pageNumber * pageSize);
        AggregationOperation limitAgg = Aggregation.limit(pageSize);

        // final aggreagation
        Aggregation aggregation = Aggregation.newAggregation(
                matchAgg, lookupAggStep, unwindAggStep, lookupAggService, unwindAggService, sortAgg, skipAgg, limitAgg
        );

        // query database
        AggregationResults<Job> obj = mongoTemplate.aggregate(aggregation, mongoTemplate.getCollectionName(Job.class), Job.class);
        java.util.List<Job> results = obj.getMappedResults();

        return new PageImpl<>(
                results.stream().map(JobReturnDto::map).collect(Collectors.toList()),
                pageable,
                mongoTemplate.count(new Query(criteria), Job.class)
        );
    }

    @Override
    public JobReturnDto findById(Long idValue) {
        AggregationOperation lookupAggStep = Aggregation.lookup("step", "stepId", "_id", "step");
        AggregationOperation unwindAggStep = Aggregation.unwind("step", true);
        AggregationOperation lookupAggService = Aggregation.lookup("service", "step.serviceId", "_id", "step.service");
        AggregationOperation unwindAggService = Aggregation.unwind("step.service", true);

        Criteria criteria = new Criteria();
        criteria.and("_id").is(idValue);
        AggregationOperation matchAgg = Aggregation.match(criteria);

        Aggregation aggregation = Aggregation.newAggregation(
                lookupAggStep,
                unwindAggStep,
                lookupAggService,
                lookupAggService,
                unwindAggService,
                matchAgg
        );

        // query database
        AggregationResults<Job> obj = mongoTemplate.aggregate(
                aggregation, GlobalParams.JOB_COLLECTION_NAME, Job.class
        );
        java.util.List<Job> results = obj.getMappedResults();

        return JobReturnDto.map(results.stream().findFirst().orElseThrow(() -> new ObjectNotFoundException("id")));
    }

    @Override
    public void delete(Long idValue) {
        Job job = jobInputDS.findById(idValue).orElseThrow(() -> new ObjectNotFoundException("id"));
        if (!recordJobInputDS.findAllByJob(idValue).isEmpty()) {
            throw new ObjectExistsException("recordJob");
        }
        jobInputDS.delete(job);
    }
}
