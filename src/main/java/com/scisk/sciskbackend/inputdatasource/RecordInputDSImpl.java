package com.scisk.sciskbackend.inputdatasource;

import com.scisk.sciskbackend.datasourceentity.RecordDS;
import com.scisk.sciskbackend.entity.Record;
import com.scisk.sciskbackend.repository.RecordRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Transactional
@Service
public class RecordInputDSImpl implements RecordInputDS {

    private final RecordRepository recordRepository;

    public RecordInputDSImpl(RecordRepository recordRepository) {
        this.recordRepository = recordRepository;
    }

    @Override
    public List<Record> findAllByCreatedOnBetween(Instant start, Instant end) {
        return recordRepository.findAllByCreatedOnBetween(start, end)
                .stream()
                .map(RecordDS::map)
                .collect(Collectors.toList());
    }

    @Override
    public List<Record> findAllByCode(String code) {
        return recordRepository.findAllByCode(code).stream().map(RecordDS::map).collect(Collectors.toList());
    }

    @Override
    public Record save(Record record) {
        recordRepository.save(RecordDS.map(record));
        return record;
    }

    @Override
    public Optional<Record> findById(Long idValue) {
        return recordRepository.findById(idValue).map(RecordDS::map);
    }
}
