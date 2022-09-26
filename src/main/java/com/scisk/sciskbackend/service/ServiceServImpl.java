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
import java.util.*;
import java.util.stream.Collectors;

@Transactional
@Service
public class ServiceServImpl implements ServiceService {

    private UserRepository userRepository;
    private CounterService counterService;
    private StepRepository stepRepository;
    private NeededDocumentRepository neededDocumentRepository;
    private final MongoTemplate mongoTemplate;
    private final ServiceRepository serviceRepository;

    public ServiceServImpl(
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
    public ServiceReturnDto create(ServiceCreateDto serviceCreateDto) {
        // on enregistre le service
        com.scisk.sciskbackend.entity.Service service = com.scisk.sciskbackend.entity.Service.builder()
                .name(serviceCreateDto.getName())
                .description(serviceCreateDto.getDescription())
                .enabled(serviceCreateDto.getEnabled())
                .build();

        service.setId(counterService.getNextSequence(GlobalParams.SERVICE_COLLECTION_NAME));
        service.setCreatedOn(Instant.now());
        serviceRepository.save(service);

        // on enregistre les étapes
        if (!serviceCreateDto.getSteps().isEmpty()) {
            service.setSteps(new ArrayList<>());
            for (StepCreateDto stepCreateDto : serviceCreateDto.getSteps()) {
                Step step = Step.builder()
                        .name(stepCreateDto.getName())
                        .description(stepCreateDto.getDescription())
                        .order(stepCreateDto.getOrder())
                        .enabled(stepCreateDto.getEnabled())
                        .serviceId(service.getId())
                        .service(service)
                        .build();

                step.setId(counterService.getNextSequence(GlobalParams.STEP_COLLECTION_NAME));
                stepRepository.save(step);
                service.getSteps().add(step);
            }
        }

        // on enregistre les documents nécessaires
        if (!serviceCreateDto.getNeededDocuments().isEmpty()) {
            service.setNeededDocuments(new ArrayList<>());
            for (NeededDocumentCreateDto neededDocumentCreateDto : serviceCreateDto.getNeededDocuments()) {
                NeededDocument neededDocument = NeededDocument.builder()
                        .name(neededDocumentCreateDto.getName())
                        .enabled(neededDocumentCreateDto.getEnabled())
                        .serviceId(service.getId())
                        .service(service)
                        .build();

                neededDocument.setId(counterService.getNextSequence(GlobalParams.NEEDED_DOCUMENT_COLLECTION_NAME));
                neededDocumentRepository.save(neededDocument);
                service.getNeededDocuments().add(neededDocument);
            }
        }

        return ServiceReturnDto.map(service);
    }

    @Override
    public ServiceReturnDto update(Long idValue, ServiceUpdateDto serviceUpdateDto) {
        com.scisk.sciskbackend.entity.Service service = serviceRepository.findById(idValue).orElseThrow(() -> new ObjectNotFoundException("id"));

        service.setName(serviceUpdateDto.getName());
        service.setDescription(serviceUpdateDto.getDescription());
        service.setEnabled(serviceUpdateDto.getEnabled());

        service = serviceRepository.save(service);
        return ServiceReturnDto.map(service);
    }

    @Override
    public Page<ServiceReturnDto> findAllServiceByFilters(Integer page, Integer size, String name, String description) {
        int pageNumber = Objects.isNull(page) ? 0 : page - 1;
        int pageSize = Objects.isNull(size) ? GlobalParams.GLOBAL_DEFAULT_PAGE_SIZE : size;
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.Direction.DESC, "createdOn");

        // create lookup aggregations
        AggregationOperation lookupAggSteps = Aggregation.lookup("step", "_id", "serviceId", "steps");
        AggregationOperation lookupAggNeededDocuements = Aggregation.lookup("neededdocument", "_id", "serviceId", "neededDocuments");

        // create criteria filters
        Criteria criteria = new Criteria();

        if (StringUtils.isNotBlank(name)) {
            criteria.and("name").regex(name, "i");
        }

        if (StringUtils.isNotBlank(description)) {
            criteria.and("description").regex(description, "i");
        }

        // create match aggregation
        AggregationOperation matchAgg = Aggregation.match(criteria);

        // create sort aggregation
        AggregationOperation sortAgg = Aggregation.sort(Sort.Direction.DESC, "createdOn");

        // create skip aggregation
        long elementToSkip = pageNumber * pageSize;
        AggregationOperation skipAgg = Aggregation.skip(elementToSkip);

        // create limit aggreagtion
        AggregationOperation limitAgg = Aggregation.limit(pageSize);

        // final aggreagation
        Aggregation aggregation = Aggregation.newAggregation(lookupAggSteps, lookupAggNeededDocuements, matchAgg, sortAgg, skipAgg, limitAgg);

        // query database
        AggregationResults<com.scisk.sciskbackend.entity.Service> obj = mongoTemplate.aggregate(
                aggregation, GlobalParams.SERVICE_COLLECTION_NAME, com.scisk.sciskbackend.entity.Service.class
        );
        java.util.List<com.scisk.sciskbackend.entity.Service> results = obj.getMappedResults();

        return new PageImpl<>(
                results.stream().map(ServiceReturnDto::map).collect(Collectors.toList()),
                pageable,
                mongoTemplate.count(new Query(criteria), com.scisk.sciskbackend.entity.Service.class)
        );
    }

    @Override
    public Page<ServiceReturnDto> findAllForCustomers(Integer page, Integer size, String name, String description) {
        int pageNumber = Objects.isNull(page) ? 0 : page - 1;
        int pageSize = Objects.isNull(size) ? GlobalParams.GLOBAL_DEFAULT_PAGE_SIZE : size;
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.Direction.DESC, "createdOn");

        // create lookup aggregations
        AggregationOperation lookupAggSteps = Aggregation.lookup("step", "_id", "serviceId", "steps");
        AggregationOperation lookupAggNeededDocuements = Aggregation.lookup("neededdocument", "_id", "serviceId", "neededDocuments");

        // create criteria filters
        Criteria criteria = new Criteria();

        if (StringUtils.isNotBlank(name)) {
            criteria.and("name").regex(name, "i");
        }

        if (StringUtils.isNotBlank(description)) {
            criteria.and("description").regex(description, "i");
        }

        // on filtre uniquement les documents activés
        criteria.and("enabled").is(true);

        // create match aggregation
        AggregationOperation matchAgg = Aggregation.match(criteria);

        // create sort aggregation
        AggregationOperation sortAgg = Aggregation.sort(Sort.Direction.DESC, "createdOn");

        // create skip aggregation
        long elementToSkip = pageNumber * pageSize;
        AggregationOperation skipAgg = Aggregation.skip(elementToSkip);

        // create limit aggreagtion
        AggregationOperation limitAgg = Aggregation.limit(pageSize);

        // final aggreagation
        Aggregation aggregation = Aggregation.newAggregation(lookupAggSteps, lookupAggNeededDocuements, matchAgg, sortAgg, skipAgg, limitAgg);

        // query database
        AggregationResults<com.scisk.sciskbackend.entity.Service> obj = mongoTemplate.aggregate(
                aggregation, GlobalParams.SERVICE_COLLECTION_NAME, com.scisk.sciskbackend.entity.Service.class
        );
        java.util.List<com.scisk.sciskbackend.entity.Service> results = obj.getMappedResults();

        // dans les tableaux steps et neededDocuments on retire les documents inactivés
        for (com.scisk.sciskbackend.entity.Service service : results) {
            service.setSteps(service.getSteps().stream().filter(step -> step.getEnabled().equals(true)).collect(Collectors.toList()));
            service.setNeededDocuments(service.getNeededDocuments().stream().filter(neededDocument -> neededDocument.getEnabled().equals(true)).collect(Collectors.toList()));
        }

        return new PageImpl<>(
                results.stream().map(ServiceReturnDto::map).collect(Collectors.toList()),
                pageable,
                mongoTemplate.count(new Query(criteria), com.scisk.sciskbackend.entity.Service.class)
        );
    }

    @Override
    public ServiceReturnDto findById(Long idValue) {
        return ServiceReturnDto.map(getById(idValue));
    }

    @Override
    public StepReturnDto createStep(Long idValue, StepCreateDto stepCreateDto) {
        com.scisk.sciskbackend.entity.Service service = getById(idValue);

        Step step = Step.builder()
                .id(counterService.getNextSequence(GlobalParams.STEP_COLLECTION_NAME))
                .name(stepCreateDto.getName())
                .description(stepCreateDto.getDescription())
                .order(stepCreateDto.getOrder())
                .enabled(stepCreateDto.getEnabled())
                .service(service)
                .serviceId(service.getId())
                .build();

        return StepReturnDto.map(stepRepository.save(step));
    }

    @Override
    public NeededDocumentReturnDto createNeededDocument(Long idValue, NeededDocumentCreateDto neededDocumentCreateDto) {
        com.scisk.sciskbackend.entity.Service service = getById(idValue);

        NeededDocument neededDocument = NeededDocument.builder()
                .id(counterService.getNextSequence(GlobalParams.NEEDED_DOCUMENT_COLLECTION_NAME))
                .name(neededDocumentCreateDto.getName())
                .enabled(neededDocumentCreateDto.getEnabled())
                .serviceId(service.getId())
                .service(service)
                .build();

        return NeededDocumentReturnDto.map(neededDocumentRepository.save(neededDocument));
    }

    @Override
    public StepReturnDto updateStep(Long idValue, Long stepIdValue, StepCreateDto stepCreateDto) {
        Step step = stepRepository.findById(stepIdValue).orElseThrow(() -> new ObjectNotFoundException("stepId"));

        if (!step.getServiceId().equals(idValue)) {
            throw new ObjectNotFoundException("service.id");
        }

        step.setName(stepCreateDto.getName());
        step.setDescription(stepCreateDto.getDescription());
        step.setOrder(stepCreateDto.getOrder());
        step.setEnabled(stepCreateDto.getEnabled());

        return StepReturnDto.map(stepRepository.save(step));
    }

    @Override
    public NeededDocumentReturnDto updateNeededDocument(Long idValue, Long neededDocumentIdValue, NeededDocumentCreateDto neededDocumentCreateDto) {
        NeededDocument neededDocument = neededDocumentRepository.findById(neededDocumentIdValue).orElseThrow(() -> new ObjectNotFoundException("neededDocumentId"));

        if (!neededDocument.getServiceId().equals(idValue)) {
            throw new ObjectNotFoundException("service.id");
        }

        neededDocument.setName(neededDocumentCreateDto.getName());
        neededDocument.setEnabled(neededDocumentCreateDto.getEnabled());

        return NeededDocumentReturnDto.map(neededDocumentRepository.save(neededDocument));
    }

    public com.scisk.sciskbackend.entity.Service getById(Long id) {
        // create lookup aggregations
        AggregationOperation lookupAggSteps = Aggregation.lookup("step", "_id", "serviceId", "steps");
        AggregationOperation lookupAggNeededDocuements = Aggregation.lookup("neededdocument", "_id", "serviceId", "neededDocuments");

        // create criteria filters
        Criteria criteria = new Criteria();
        criteria.and("_id").is(id);

        // create match aggregation
        AggregationOperation matchAgg = Aggregation.match(criteria);

        // final aggreagation
        Aggregation aggregation = Aggregation.newAggregation(lookupAggSteps, lookupAggNeededDocuements, matchAgg);

        // query database
        AggregationResults<com.scisk.sciskbackend.entity.Service> obj = mongoTemplate.aggregate(
                aggregation, GlobalParams.SERVICE_COLLECTION_NAME, com.scisk.sciskbackend.entity.Service.class
        );
        java.util.List<com.scisk.sciskbackend.entity.Service> results = obj.getMappedResults();

        return results.stream().findFirst().orElseThrow(() -> new ObjectNotFoundException("id"));
    }

}
