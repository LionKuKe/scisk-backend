package com.scisk.sciskbackend.service;

import com.scisk.sciskbackend.dto.*;
import com.scisk.sciskbackend.entity.*;
import com.scisk.sciskbackend.exception.ObjectExistsException;
import com.scisk.sciskbackend.exception.ObjectNotFoundException;
import com.scisk.sciskbackend.inputdatasource.*;
import com.scisk.sciskbackend.util.AuthenticationUtil;
import com.scisk.sciskbackend.util.GlobalParams;
import org.apache.commons.io.FilenameUtils;
import org.bson.BsonBinarySubType;
import org.bson.types.Binary;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URLConnection;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Transactional
@Service
public class RecordServImpl implements RecordService {

    private UserInputDS userInputDS;
    private CounterService counterService;
    private StepInputDS stepInputDS;
    private NeededDocumentInputDS neededDocumentInputDS;
    private final MongoTemplate mongoTemplate;
    private final ServiceInputDS serviceInputDS;
    private final ServiceService serviceService;
    private final RecordInputDS recordInputDS;
    private final RecordStepInputDS recordStepInputDS;
    private final RecordJobInputDS recordJobInputDS;
    private final JobInputDS jobInputDS;
    private final PaymentInputDS paymentInputDS;
    private final AuthenticationUtil authenticationUtil;

    public RecordServImpl(
            UserInputDS userInputDS,
            CounterService counterService,
            StepInputDS stepInputDS,
            NeededDocumentInputDS neededDocumentInputDS,
            ServiceInputDS serviceInputDS,
            MongoTemplate mongoTemplate,
            ServiceService serviceService,
            RecordInputDS recordInputDS,
            RecordStepInputDS recordStepInputDS,
            RecordJobInputDS recordJobInputDS,
            JobInputDS jobInputDS,
            PaymentInputDS paymentInputDS, AuthenticationUtil authenticationUtil) {
        this.userInputDS = userInputDS;
        this.counterService = counterService;
        this.stepInputDS = stepInputDS;
        this.neededDocumentInputDS = neededDocumentInputDS;
        this.mongoTemplate = mongoTemplate;
        this.serviceInputDS = serviceInputDS;
        this.serviceService = serviceService;
        this.recordInputDS = recordInputDS;
        this.recordStepInputDS = recordStepInputDS;
        this.recordJobInputDS = recordJobInputDS;
        this.jobInputDS = jobInputDS;
        this.paymentInputDS = paymentInputDS;
        this.authenticationUtil = authenticationUtil;
    }


    @Override
    public RecordReturnDto create(RecordCreateDto recordCreateDto) {
        User customer = userInputDS.findById(recordCreateDto.getCustomerId()).orElseThrow(() -> new ObjectNotFoundException("customerId"));
        com.scisk.sciskbackend.entity.Service service = serviceService.getById(recordCreateDto.getServiceId());

        // on créé le dossier
        Record record = Record.builder()
                .id(counterService.getNextSequence(GlobalParams.RECORD_COLLECTION_NAME))
                .code(counterService.getNextCodeOfCollection(GlobalParams.RECORD_COLLECTION_NAME))
                .customer(customer)
                .service(service)
                .createdOn(Instant.now())
                .suspended(false)
                .paid(false)
                .build();
        recordInputDS.save(record);
        record.setService(service);
        record.setCustomer(customer);

        return RecordReturnDto.map(record);
    }

    @Override
    public RecordReturnDto update(Long idValue, RecordCreateDto recordCreateDto) {
        User customer = userInputDS.findById(recordCreateDto.getCustomerId()).orElseThrow(() -> new ObjectNotFoundException("customerId"));
        com.scisk.sciskbackend.entity.Service service = serviceService.getById(recordCreateDto.getServiceId());
        Record record = getById(idValue);
        if (record.getPaid()) {
            throw new ObjectExistsException("record.already.paid");
        }
        record.setCustomer(customer);
        record.setService(service);
        recordInputDS.save(record);
        return RecordReturnDto.map(record);
    }

    @Override
    public Page<RecordReturnDto> findAllRecordByFilters(Integer page, Integer size) {
        int pageNumber = Objects.isNull(page) ? 0 : page - 1;
        int pageSize = Objects.isNull(size) ? GlobalParams.GLOBAL_DEFAULT_PAGE_SIZE : size;
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.Direction.DESC, "createdOn");

        // create lookup aggregations
        AggregationOperation lookupAggPayments = Aggregation.lookup("payment", "_id", "recordId", "payments");
        AggregationOperation lookupAggRecordSteps = Aggregation.lookup("recordstep", "_id", "recordId", "recordSteps");
        AggregationOperation lookupAggCustomer = Aggregation.lookup("user", "customerId", "_id", "customer");
        AggregationOperation lookupAggManager = Aggregation.lookup("user", "managerId", "_id", "manager");
        AggregationOperation lookupAggService = Aggregation.lookup("service", "serviceId", "_id", "service");

        // create criteria filters
        Criteria criteria = new Criteria();
        //criteria.and("_id").is(idValue);

        // create match aggregation
        AggregationOperation matchAgg = Aggregation.match(criteria);

        // unwind aggregation
        AggregationOperation unwindAggCustomer = Aggregation.unwind("customer", true);
        AggregationOperation unwindAggService = Aggregation.unwind("service", true);
        AggregationOperation unwindAggManager = Aggregation.unwind("manager", true);

        AggregationOperation sortAgg = Aggregation.sort(Sort.Direction.DESC, "createdOn");
        AggregationOperation skipAgg = Aggregation.skip(pageNumber * pageSize);
        AggregationOperation limitAgg = Aggregation.limit(pageSize);

        // final aggreagation
        Aggregation aggregation = Aggregation.newAggregation(
                lookupAggPayments,
                lookupAggRecordSteps,
                lookupAggCustomer,
                lookupAggService,
                lookupAggManager,
                matchAgg,
                unwindAggCustomer,
                unwindAggService,
                unwindAggManager,
                sortAgg, skipAgg, limitAgg
        );

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

        int pageNumber = Objects.isNull(page) ? 0 : page - 1;
        int pageSize = Objects.isNull(size) ? GlobalParams.GLOBAL_DEFAULT_PAGE_SIZE : size;
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.Direction.DESC, "createdOn");

        // create lookup aggregations
        AggregationOperation lookupAggPayments = Aggregation.lookup("payment", "_id", "recordId", "payments");
        AggregationOperation lookupAggRecordSteps = Aggregation.lookup("recordstep", "_id", "recordId", "recordSteps");
        AggregationOperation lookupAggCustomer = Aggregation.lookup("user", "customerId", "_id", "customer");
        AggregationOperation lookupAggService = Aggregation.lookup("service", "serviceId", "_id", "service");

        // create criteria filters
        Criteria criteria = new Criteria();
        criteria.and("customerId").is(user.getId());

        // create match aggregation
        AggregationOperation matchAgg = Aggregation.match(criteria);

        // unwind aggregation
        AggregationOperation unwindAggCustomer = Aggregation.unwind("customer", true);
        AggregationOperation unwindAggService = Aggregation.unwind("service", true);

        AggregationOperation sortAgg = Aggregation.sort(Sort.Direction.DESC, "createdOn");
        AggregationOperation skipAgg = Aggregation.skip(pageNumber * pageSize);
        AggregationOperation limitAgg = Aggregation.limit(pageSize);

        // final aggreagation
        Aggregation aggregation = Aggregation.newAggregation(
                lookupAggPayments, lookupAggRecordSteps, lookupAggCustomer, lookupAggService,
                matchAgg, unwindAggCustomer, unwindAggService, sortAgg, skipAgg, limitAgg);

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
        AggregationOperation lookupAggCustomer = Aggregation.lookup("user", "customerId", "_id", "customer");
        AggregationOperation lookupAggService = Aggregation.lookup("service", "serviceId", "_id", "service");
        AggregationOperation lookupAggManager = Aggregation.lookup("user", "managerId", "_id", "manager");

        // create criteria filters
        Criteria criteria = new Criteria();
        criteria.and("_id").is(idValue);

        // create match aggregation
        AggregationOperation matchAgg = Aggregation.match(criteria);

        // unwind aggregation
        AggregationOperation unwindAggCustomer = Aggregation.unwind("customer", true);
        AggregationOperation unwindAggService = Aggregation.unwind("service", true);
        AggregationOperation unwindAggManager = Aggregation.unwind("manager", true);

        // final aggreagation
        Aggregation aggregation = Aggregation.newAggregation(
                lookupAggPayments,
                lookupAggRecordSteps,
                lookupAggCustomer,
                lookupAggService,
                lookupAggManager,
                matchAgg,
                unwindAggCustomer,
                unwindAggService,
                unwindAggManager
        );

        // query database
        AggregationResults<Record> obj = mongoTemplate.aggregate(
                aggregation, GlobalParams.RECORD_COLLECTION_NAME, Record.class
        );
        java.util.List<Record> results = obj.getMappedResults();

        return results.stream().findFirst().orElseThrow(() -> new ObjectNotFoundException("id"));
    }

    @Override
    public RecordReturnDto suspend(Long idValue, String reason) {
        Record record = getById(idValue);
        record.setSuspended(true);
        record.setSuspensionDate(Instant.now());
        record.setSuspensionReason(reason);
        recordInputDS.save(record);
        return RecordReturnDto.map(record);
    }

    @Override
    public void uploadDocument(Long id, MultipartFile file, String name, Long neededDocumentId) {
        Record record = getById(id);
        NeededDocument neededDocument = neededDocumentInputDS.findById(neededDocumentId).orElseThrow(() -> new ObjectNotFoundException("neddedDocumentId"));
        if (Objects.isNull(record.getDocuments())) {
            record.setDocuments(new ArrayList<>());
        }
        try {
            Document document = Document.builder()
                    .id(counterService.getNextSequence(GlobalParams.DOCUMENT_COLLECTION_NAME))
                    .name(name)
                    .extension(FilenameUtils.getExtension(file.getOriginalFilename()))
                    .content(new Binary(BsonBinarySubType.BINARY, file.getBytes()))
                    .neededDocumentId(neededDocument.getId())
                    .build();
            record.getDocuments().add(document);
            recordInputDS.save(record);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteDocument(Long id, Long documentId) {
        Record record = getById(id);
        record.getDocuments().stream().filter(document -> document.getId().equals(documentId)).findFirst().orElseThrow(() -> new ObjectNotFoundException("documentId"));
        List<Document> documents = record.getDocuments().stream().filter(document -> !document.getId().equals(documentId)).collect(Collectors.toList());
        record.setDocuments(documents);
        recordInputDS.save(record);
    }

    @Override
    public ResponseEntity<Resource> downloadDocument(HttpServletRequest request, Long documentId) {
        Record record = recordInputDS.findByDocumentId(documentId).orElseThrow(() -> new ObjectNotFoundException("documentId"));
        Document document = record.getDocuments().stream().filter(document1 -> document1.getId().equals(documentId)).findFirst().orElseThrow(() -> new ObjectNotFoundException("documentId"));

        Resource resource = new ByteArrayResource(document.getContent().getData(), document.getName());

        // Try to determine file's content type
        String contentType = URLConnection.guessContentTypeFromName(resource.getFilename());
        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, contentType);
        headers.add(HttpHeaders.CONTENT_DISPOSITION, resource.getFilename());

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .headers(headers)
                .body(resource);
    }
}
