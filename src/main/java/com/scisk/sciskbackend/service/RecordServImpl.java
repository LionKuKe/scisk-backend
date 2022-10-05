package com.scisk.sciskbackend.service;

import com.scisk.sciskbackend.dto.*;
import com.scisk.sciskbackend.entity.*;
import com.scisk.sciskbackend.exception.ObjectExistsException;
import com.scisk.sciskbackend.exception.ObjectNotFoundException;
import com.scisk.sciskbackend.repository.*;
import com.scisk.sciskbackend.util.AuthenticationUtil;
import com.scisk.sciskbackend.util.GlobalParams;
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
    private final ServiceService serviceService;
    private final RecordRepository recordRepository;
    private final RecordStepRepository recordStepRepository;
    private final RecordJobRepository recordJobRepository;
    private final JobRepository jobRepository;
    private final PaymentRepository paymentRepository;
    private final AuthenticationUtil authenticationUtil;

    public RecordServImpl(
            UserRepository userRepository,
            CounterService counterService,
            StepRepository stepRepository,
            NeededDocumentRepository neededDocumentRepository,
            ServiceRepository serviceRepository,
            MongoTemplate mongoTemplate,
            ServiceService serviceService,
            RecordRepository recordRepository,
            RecordStepRepository recordStepRepository,
            RecordJobRepository recordJobRepository,
            JobRepository jobRepository,
            PaymentRepository paymentRepository, AuthenticationUtil authenticationUtil) {
        this.userRepository = userRepository;
        this.counterService = counterService;
        this.stepRepository = stepRepository;
        this.neededDocumentRepository = neededDocumentRepository;
        this.mongoTemplate = mongoTemplate;
        this.serviceRepository = serviceRepository;
        this.serviceService = serviceService;
        this.recordRepository = recordRepository;
        this.recordStepRepository = recordStepRepository;
        this.recordJobRepository = recordJobRepository;
        this.jobRepository = jobRepository;
        this.paymentRepository = paymentRepository;
        this.authenticationUtil = authenticationUtil;
    }


    @Override
    public RecordReturnDto create(RecordCreateDto recordCreateDto) {
        User customer = userRepository.findById(recordCreateDto.getCustomerId()).orElseThrow(() -> new ObjectNotFoundException("customerId"));
        com.scisk.sciskbackend.entity.Service service = serviceService.getById(recordCreateDto.getServiceId());

        // on créé le dossier
        Record record = Record.builder()
                .id(counterService.getNextSequence(GlobalParams.RECORD_COLLECTION_NAME))
                .code(counterService.getNextCodeOfCollection(GlobalParams.RECORD_COLLECTION_NAME))
                .customerId(customer.getId())
                .serviceId(service.getId())
                .createdOn(Instant.now())
                .suspended(false)
                .paid(false)
                .build();
        recordRepository.save(record);
        record.setService(service);
        record.setCustomer(customer);

        return RecordReturnDto.map(recordRepository.save(record));
    }

    @Override
    public RecordReturnDto update(Long idValue, RecordCreateDto recordCreateDto) {
        User customer = userRepository.findById(recordCreateDto.getCustomerId()).orElseThrow(() -> new ObjectNotFoundException("customerId"));
        com.scisk.sciskbackend.entity.Service service = serviceService.getById(recordCreateDto.getServiceId());
        Record record = recordRepository.findById(idValue).orElseThrow(() -> new ObjectNotFoundException("id"));
        if (record.getPaid()) {
            throw new ObjectExistsException("record.already.paid");
        }
        record.setCustomer(customer);
        record.setService(service);
        recordRepository.save(record);
        return RecordReturnDto.map(recordRepository.save(record));
    }

    @Override
    public Page<RecordReturnDto> findAllRecordByFilters(Integer page, Integer size) {
        Pageable pageable = PageRequest.of(
                Objects.isNull(page) ? 0 : page - 1,
                Objects.isNull(size) ? GlobalParams.GLOBAL_DEFAULT_PAGE_SIZE : size
        );

        // create lookup aggregations
        AggregationOperation lookupAggPayments = Aggregation.lookup("payment", "_id", "recordId", "payments");
        AggregationOperation lookupAggRecordSteps = Aggregation.lookup("recordstep", "_id", "recordId", "recordSteps");

        // create criteria filters
        Criteria criteria = new Criteria();
        //criteria.and("_id").is(idValue);

        // create match aggregation
        AggregationOperation matchAgg = Aggregation.match(criteria);

        // final aggreagation
        Aggregation aggregation = Aggregation.newAggregation(lookupAggPayments, lookupAggRecordSteps, matchAgg);

        // query database
        AggregationResults<Record> obj = mongoTemplate.aggregate(aggregation, mongoTemplate.getCollectionName(Record.class), Record.class);
        java.util.List<Record> results = obj.getMappedResults();

        return new PageImpl<>(
                results.stream().map(RecordReturnDto::map).collect(Collectors.toList()),
                pageable,
                mongoTemplate.count(new Query(criteria), Record.class)
        );
    }

    @Override
    public Page<RecordReturnDto> findAllForCustomers(Integer page, Integer size) {
        User user = authenticationUtil.getConnectedUser().orElseThrow(() -> new ObjectNotFoundException("connected.user"));

        Pageable pageable = PageRequest.of(
                Objects.isNull(page) ? 0 : page - 1,
                Objects.isNull(size) ? GlobalParams.GLOBAL_DEFAULT_PAGE_SIZE : size
        );

        // create lookup aggregations
        AggregationOperation lookupAggPayments = Aggregation.lookup("payment", "_id", "recordId", "payments");
        AggregationOperation lookupAggRecordSteps = Aggregation.lookup("recordstep", "_id", "recordId", "recordSteps");

        // create criteria filters
        Criteria criteria = new Criteria();
        criteria.and("customerId").is(user.getId());

        // create match aggregation
        AggregationOperation matchAgg = Aggregation.match(criteria);

        // final aggreagation
        Aggregation aggregation = Aggregation.newAggregation(lookupAggPayments, lookupAggRecordSteps, matchAgg);

        // query database
        AggregationResults<Record> obj = mongoTemplate.aggregate(aggregation, mongoTemplate.getCollectionName(Record.class), Record.class);
        java.util.List<Record> results = obj.getMappedResults();

        return new PageImpl<>(
                results.stream().map(RecordReturnDto::map).collect(Collectors.toList()),
                pageable,
                mongoTemplate.count(new Query(criteria), Record.class)
        );
    }

    @Override
    public RecordReturnDto findById(Long idValue) {
        return RecordReturnDto.map(getById(idValue));
    }

    @Override
    public Record getById(Long idValue) {
        // create lookup aggregations
        AggregationOperation lookupAggPayments = Aggregation.lookup("payment", "_id", "recordId", "payments");
        AggregationOperation lookupAggRecordSteps = Aggregation.lookup("recordstep", "_id", "recordId", "recordSteps");

        // create criteria filters
        Criteria criteria = new Criteria();
        criteria.and("_id").is(idValue);

        // create match aggregation
        AggregationOperation matchAgg = Aggregation.match(criteria);

        // final aggreagation
        Aggregation aggregation = Aggregation.newAggregation(lookupAggPayments, lookupAggRecordSteps, matchAgg);

        // query database
        AggregationResults<Record> obj = mongoTemplate.aggregate(
                aggregation, GlobalParams.RECORD_COLLECTION_NAME, Record.class
        );
        java.util.List<Record> results = obj.getMappedResults();

        return results.stream().findFirst().orElseThrow(() -> new ObjectNotFoundException("id"));
    }

    @Override
    public RecordReturnDto suspend(Long idValue, String reason) {
        Record record = recordRepository.findById(idValue).orElseThrow(() -> new ObjectNotFoundException("id"));
        record.setSuspended(true);
        record.setSuspensionDate(Instant.now());
        record.setSuspensionReason(reason);
        recordRepository.save(record);
        return RecordReturnDto.map(record);
    }
}
