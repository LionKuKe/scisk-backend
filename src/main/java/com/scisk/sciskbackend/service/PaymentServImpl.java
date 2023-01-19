package com.scisk.sciskbackend.service;

import com.scisk.sciskbackend.dto.*;
import com.scisk.sciskbackend.entity.*;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Transactional
@Service
public class PaymentServImpl implements PaymentService {

    private CounterService counterService;
    private final MongoTemplate mongoTemplate;
    private final RecordInputDS recordInputDS;
    private final PaymentInputDS paymentInputDS;
    private final ServiceService serviceService;
    private final RecordStepInputDS recordStepInputDS;
    private final JobInputDS jobInputDS;
    private final RecordJobInputDS recordJobInputDS;
    private final RecordService recordService;

    public PaymentServImpl(
            CounterService counterService,
            MongoTemplate mongoTemplate,
            RecordInputDS recordInputDS,
            PaymentInputDS paymentInputDS,
            ServiceService serviceService,
            RecordStepInputDS recordStepInputDS,
            JobInputDS jobInputDS,
            RecordJobInputDS recordJobInputDS, RecordService recordService) {
        this.counterService = counterService;
        this.mongoTemplate = mongoTemplate;
        this.recordInputDS = recordInputDS;
        this.paymentInputDS = paymentInputDS;
        this.serviceService = serviceService;
        this.recordStepInputDS = recordStepInputDS;
        this.jobInputDS = jobInputDS;
        this.recordJobInputDS = recordJobInputDS;
        this.recordService = recordService;
    }


    @Override
    public PaymentReturnDto create(PaymentCreateDto paymentCreateDto) {
        Record record = recordService.getById(paymentCreateDto.getRecordId());

        Payment payment = Payment.builder()
                .id(counterService.getNextSequence(GlobalParams.PAYMENT_COLLECTION_NAME))
                .paymentDate(paymentCreateDto.getPaymentDate())
                .amount(paymentCreateDto.getAmount())
                .observation(paymentCreateDto.getObservation())
                .record(record)
                .build();
        paymentInputDS.save(payment);

        // si les paiements sur le dossier atteignent le motant minimum de traitement de dossier on marque le dossier comme payé
        List<Payment> paymentList = paymentInputDS.findByRecordId(record.getId());
        Double paidAmount = paymentCreateDto.getAmount() + (paymentList.isEmpty() ? 0 : paymentList.stream().mapToDouble(Payment::getAmount).sum());
        if (GlobalParams.MIN_AMOUNT_FOR_RECORD_OPENING.compareTo(paidAmount) < 0) {
            record.setPaid(true);
            recordInputDS.save(record);
        }

        // on créé les étapes et les taches du dossier
        if (Objects.isNull(record.getRecordSteps()) || record.getRecordSteps().size() == 0) {
            createRecordStepsAndJobs(record);
        }

        return PaymentReturnDto.map(payment);
    }

    @Override
    public PaymentReturnDto update(Long idValue, PaymentCreateDto paymentCreateDto) {
        Payment payment = paymentInputDS.findById(idValue).orElseThrow(() -> new ObjectNotFoundException("id"));
        Record record = recordService.getById(paymentCreateDto.getRecordId());

        payment.setPaymentDate(paymentCreateDto.getPaymentDate());
        payment.setAmount(paymentCreateDto.getAmount());
        payment.setObservation(paymentCreateDto.getObservation());
        payment.setRecord(record);
        paymentInputDS.save(payment);

        // si les paiements sur le dossier atteignent le motant minimum de traitement de dossier on marque le dossier comme payé
        List<Payment> paymentList = paymentInputDS.findByRecordId(record.getId());
        Double paidAmount = paymentCreateDto.getAmount() + (paymentList.isEmpty() ? 0 : paymentList.stream().filter(pay -> !pay.getId().equals(idValue)).mapToDouble(Payment::getAmount).sum());
        if (GlobalParams.MIN_AMOUNT_FOR_RECORD_OPENING.compareTo(paidAmount) < 0) {
            record.setPaid(true);
        } else {
            record.setPaid(false);
        }
        recordInputDS.save(record);

        // on créé les étapes et les taches du dossier
        if (Objects.isNull(record.getRecordSteps()) || record.getRecordSteps().size() == 0) {
            createRecordStepsAndJobs(record);
        }

        return PaymentReturnDto.map(payment);
    }

    @Override
    public Page<PaymentReturnDto> findAllPaymentByFilters(Integer page, Integer size, String observation, Long recordId) {
        int pageNumber = Objects.isNull(page) ? 0 : page - 1;
        int pageSize = Objects.isNull(size) ? GlobalParams.GLOBAL_DEFAULT_PAGE_SIZE : size;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        AggregationOperation lookupAggRecord = Aggregation.lookup("record", "recordId", "_id", "record");

        Query query = new Query();
        query.with(pageable);
        Criteria criteria = new Criteria();

        if (StringUtils.isNotBlank(observation)) {
            criteria.and("observation").regex(observation, "i");
        }

        if (!Objects.isNull(recordId)) {
            criteria.and("recordId").is(recordId);
        }

        AggregationOperation matchAgg = Aggregation.match(criteria);

        AggregationOperation unwindAggRecord = Aggregation.unwind("record", true);

        AggregationOperation sortAgg = Aggregation.sort(Sort.Direction.DESC, "createdOn");
        AggregationOperation skipAgg = Aggregation.skip(pageNumber * pageSize);
        AggregationOperation limitAgg = Aggregation.limit(pageSize);

        Aggregation aggregation = Aggregation.newAggregation(
                lookupAggRecord,
                matchAgg,
                unwindAggRecord,
                sortAgg, skipAgg, limitAgg
        );

        // query database
        AggregationResults<Payment> obj = mongoTemplate.aggregate(aggregation, mongoTemplate.getCollectionName(Payment.class), Payment.class);
        java.util.List<Payment> results = obj.getMappedResults();

        return new PageImpl<>(
                results.stream().map(PaymentReturnDto::map).collect(Collectors.toList()),
                pageable,
                mongoTemplate.count(new Query(criteria), Payment.class)
        );
    }

    @Override
    public PaymentReturnDto findById(Long idValue) {
        // create lookup aggregations
        AggregationOperation lookupAggRecords = Aggregation.lookup("record", "recordId", "_id", "record");

        // create criteria filters
        Criteria criteria = new Criteria();
        criteria.and("_id").is(idValue);

        // create match aggregation
        AggregationOperation matchAgg = Aggregation.match(criteria);

        // unwind aggregation
        AggregationOperation unwindAggRecords = Aggregation.unwind("record", true);

        // final aggreagation
        Aggregation aggregation = Aggregation.newAggregation(
                lookupAggRecords,
                matchAgg,
                unwindAggRecords
        );

        // query database
        AggregationResults<Payment> obj = mongoTemplate.aggregate(
                aggregation, GlobalParams.PAYMENT_COLLECTION_NAME, Payment.class
        );
        java.util.List<Payment> results = obj.getMappedResults();

        return PaymentReturnDto.map(results.stream().findFirst().orElseThrow(() -> new ObjectNotFoundException("id")));

    }

    @Override
    public void delete(Long idValue) {
        Payment payment = paymentInputDS.findById(idValue).orElseThrow(() -> new ObjectNotFoundException("id"));
        paymentInputDS.delete(payment);
    }

    public void createRecordStepsAndJobs(Record record) {
        // on créé les étapes du dossier
        com.scisk.sciskbackend.entity.Service service = serviceService.getById(record.getService().getId());
        RecordStep recordStep;
        record.setRecordSteps(new ArrayList<>());
        for (Step step : service.getSteps()) {
            recordStep = RecordStep.builder()
                    .id(counterService.getNextSequence(GlobalParams.STEP_COLLECTION_NAME))
                    .name(step.getName())
                    .observation(step.getDescription())
                    .record(record)
                    .step(step)
                    .build();
            record.getRecordSteps().add(recordStep);
        }
        recordStepInputDS.saveAll(record.getRecordSteps());

        // on créé les taches
        RecordJob recordJob;
        List<Job> jobs;
        for (RecordStep recStep : record.getRecordSteps()) {
            jobs = jobInputDS.findAllByStepId(recStep.getStep().getId());
            recStep.setRecordJobs(new ArrayList<>());
            for (Job job : jobs) {
                recordJob = RecordJob.builder()
                        .id(counterService.getNextSequence(GlobalParams.JOB_COLLECTION_NAME))
                        .recordStep(recStep)
                        .job(job)
                        .build();
                recStep.getRecordJobs().add(recordJob);
            }
            recordJobInputDS.saveAll(recStep.getRecordJobs());
        }
    }
}
