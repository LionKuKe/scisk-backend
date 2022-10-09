package com.scisk.sciskbackend.inputdatasource;

import com.scisk.sciskbackend.entity.Record;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface RecordInputDS {

    List<Record> findAllByCreatedOnBetween(Instant start, Instant end);

    List<Record> findAllByCode(String code);

    Record save(Record record);

    Optional<Record> findById(Long idValue);
}
