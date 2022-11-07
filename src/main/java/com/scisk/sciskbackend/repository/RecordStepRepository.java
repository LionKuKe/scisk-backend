package com.scisk.sciskbackend.repository;

import com.scisk.sciskbackend.datasourceentity.RecordStepDS;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecordStepRepository extends MongoRepository<RecordStepDS, Long> {

    @Query("{recordId: ?0}")
    List<RecordStepDS> findAllByRecordId(Long recordId);

}
