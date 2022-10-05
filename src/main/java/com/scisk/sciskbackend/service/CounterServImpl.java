package com.scisk.sciskbackend.service;

import com.scisk.sciskbackend.entity.Counter;
import com.scisk.sciskbackend.entity.Record;
import com.scisk.sciskbackend.exception.ObjectExistsException;
import com.scisk.sciskbackend.repository.RecordRepository;
import com.scisk.sciskbackend.util.GlobalParams;
import com.scisk.sciskbackend.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.Month;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalField;
import java.util.List;

import static org.springframework.data.mongodb.core.FindAndModifyOptions.options;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@Transactional
@Service
public class CounterServImpl implements CounterService {

    @Autowired
    private MongoOperations mongo;

    @Autowired
    private RecordRepository recordRepository;

    @Override
    public long getNextSequence(String collectionName) {
        Counter counter = mongo.findAndModify(
                query(where("_id").is(collectionName)),
                new Update().inc("seq", 1),
                options().returnNew(true),
                Counter.class);
        return counter.getSeq();
    }

    @Override
    public String getNextCodeOfCollection(String collectionName) {
        Instant now = Instant.now();
        int year = Util.getYearFromInstant(now);
        int month = Util.getMonthFromInstant(now);
        int day = Util.getDayOfMonthFromInstant(now);
        Instant start = Util.getFirstDayOfMonth( year , Month.of(month) );
        Instant end = Util.getLastDayOfMonth(year, Month.of(month));
        List<Record> records = recordRepository.findAllByCreatedOnBetween(start, end);
        String suffix = Util.addZerosInFrontOfValue((long) records.size()+1, GlobalParams.MAX_RECORD_NUMBER_OF_DIGIT_PER_MONTH);
        String nextCode = String.format("%s%s%s%s%s",
                Util.COLLECTION_NAME_COLLECTION_CODE_MAP.get(collectionName),
                Integer.toString(year).substring(2, 2),
                Util.addZerosInFrontOfValue((long) month, 2),
                Util.addZerosInFrontOfValue((long) day, 2),
                suffix
        );
        if (recordRepository.findAllByCode(nextCode).isEmpty()) {
            return nextCode;
        } else {
            throw new ObjectExistsException("code.exists");
        }
    }
}
